package com.hampson.sharework.ui.worker.home.job_list

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.vo.Job
import com.hampson.sharework.data.vo.Tag
import com.hampson.sharework.databinding.ItemJobListBinding
import com.hampson.sharework.databinding.NetworkStateItemBinding

class BottomSheetJobPagedListAdapter(val context: Context) : PagedListAdapter<Job, RecyclerView.ViewHolder>(
    JobDiffCallback()
) {

    val JOB_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == JOB_VIEW_TYPE) {
            (holder as JobItemViewHolder).bind(getItem(position), context)
        } else {
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            JOB_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        if (viewType == JOB_VIEW_TYPE) {
            val binding = ItemJobListBinding.inflate(layoutInflater)
            return JobItemViewHolder(
                binding
            )
        } else {
            val binding = NetworkStateItemBinding.inflate(layoutInflater)
            return NetworkStateItemViewHolder(
                binding
            )
        }
    }

    class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem == newItem
        }

    }

    class JobItemViewHolder (private val binding: ItemJobListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job?, context: Context) {
            binding.textViewTitle.text = job?.job_title
            binding.textViewJobDate.text = job?.job_date
            binding.textViewJobTime.text = job?.start_date + " ~ " + job?.end_date
            binding.textViewPay.text = job?.pay
            binding.textViewJobType.text = job?.job_type
            binding.textViewPayType.text = job?.pay_type

            //Glide.with(context)
            //    .load(job?.jobable?.user?.profile_img)
            //    .circleCrop()
            //    .placeholder(R.drawable.ic_baseline_account_circle_24)
            //    .into(binding.imageViewProfile)

            val tagList = job?.tags
            if (tagList != null) {
                binding.layoutTag.removeAllViews()

                for (tag in tagList) {
                    bindTag(tag, context)
                }
            }

            //itemView.setOnClickListener {
            //    Intent(context, JobInfoActivity::class.java).apply {
            //        putExtra("jobId", job?.id)
            //        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //    }.run { context.startActivity(this) }
            //}
        }

        private fun bindTag(tag: Tag, context: Context) {
            val textView = TextView(context)
            textView.text = "#" + tag.tag_name
            //textView.backgroundResource = R.drawable.background_fill_gray
            textView.setTextColor(Color.rgb(0, 0, 0))
            textView.textSize = 14.0F
            textView.setPadding(20, 10, 20, 10)

            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(0, 0, 8, 0)
            textView.layoutParams = layoutParams
            binding.layoutTag.addView(textView)
        }
    }

    class NetworkStateItemViewHolder (private val binding: NetworkStateItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(networkState: NetworkState?) {

            if (networkState != null && networkState == NetworkState.LOADING) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                binding.textViewErrorMessage.visibility = View.VISIBLE
                binding.textViewErrorMessage.text = networkState.msg
            } else if (networkState != null && networkState == NetworkState.ENDOFLIST) {
                binding.textViewErrorMessage.visibility = View.GONE
                // textViewErrorMessage.text = networkState.msg
            } else {
                binding.textViewErrorMessage.visibility = View.GONE
            }

        }
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }
}