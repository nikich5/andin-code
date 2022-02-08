package ru.netology.nmedia.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                if (AppAuth.getInstance().authStateFlow.value.id == 0L) {
                    AlertDialog.Builder(context)
                        .setMessage(getString(R.string.sign_in_dialog_message))
                        .setNegativeButton(getString(R.string.sign_in_dialog_later)) { _, _ ->
                            return@setNegativeButton
                        }
                        .setPositiveButton(getString(R.string.sign_in_dialog_ok)) { _, _ ->
                            findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
                        }
                        .show()
                } else {
                    if (!post.likedByMe) {
                        viewModel.likeById(post.id)
                    } else {
                        viewModel.removeLikeById(post.id)
                    }
                }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
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

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        fun onRefresh() {
            viewModel.refreshPosts()
            binding.swiperefresh.isRefreshing = false
        }

        binding.swiperefresh.setOnRefreshListener {
            onRefresh()
        }

        binding.fab.setOnClickListener {
            if (AppAuth.getInstance().authStateFlow.value.id == 0L) {
                AlertDialog.Builder(context)
                    .setMessage(getString(R.string.sign_in_dialog_message))
                    .setNegativeButton(getString(R.string.sign_in_dialog_later)) { _, _ ->
                        return@setNegativeButton
                    }
                    .setPositiveButton(getString(R.string.sign_in_dialog_ok)) { _, _ ->
                        findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
                    }
                    .show()
            } else {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }
        }

        return binding.root
    }
}
