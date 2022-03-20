package ru.netology.nmedia.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {
    @Inject
    lateinit var auth: AppAuth
    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val postViewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val authViewModel: AuthViewModel by viewModels(ownerProducer = ::requireParentFragment)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)
        val dialog = AlertDialog.Builder(context)
            .setMessage(getString(R.string.sign_in_dialog_message))
            .setNegativeButton(getString(R.string.sign_in_dialog_later)) { _, _ ->
                return@setNegativeButton
            }
            .setPositiveButton(getString(R.string.sign_in_dialog_ok)) { _, _ ->
                findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
            }
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                postViewModel.edit(post)
            }
            override fun onLike(post: Post) {
                if (auth.authStateFlow.value.id == 0L) {
                    dialog.show()
                } else {
                    if (!post.likedByMe) {
                        postViewModel.likeById(post.id)
                    } else {
                        postViewModel.removeLikeById(post.id)
                    }
                }
            }

            override fun onRemove(post: Post) {
                postViewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onImage(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_imageFragment,
                    Bundle().apply
                    { textArg = post.attachment?.url })
            }
        })
        binding.list.adapter = adapter

        lifecycleScope.launchWhenCreated {
            postViewModel.data.collectLatest { state ->
                adapter.submitData(state)
            }
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            adapter.refresh()
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.swiperefresh.isRefreshing =
                    state.refresh is LoadState.Loading ||
                            state.prepend is LoadState.Loading ||
                            state.append is LoadState.Loading
            }
        }

        binding.retryButton.setOnClickListener {
            adapter.refresh()
        }

        binding.swiperefresh.setOnRefreshListener {
            adapter.refresh()
        }

        binding.fab.setOnClickListener {
            if (auth.authStateFlow.value.id == 0L) {
                dialog.show()
            } else {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }
        }

        return binding.root
    }
}
