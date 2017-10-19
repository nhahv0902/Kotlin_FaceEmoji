package com.nhahv.faceemoji.ui

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.nhahv.faceemoji.BR

/**
 * Created by nhahv0902 on 10/17/17.
 * Base Adapter for Recycler View
 */

class BaseRecyclerAdapter<T>(
    private val items: ArrayList<T>,
    private val listener: OnItemListener<T>,
    @LayoutRes val layout: Int
) : RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder<T>>() {
    private var mInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<T> {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent?.context)
        }
        val binding: ViewDataBinding = DataBindingUtil.inflate(mInflater, layout, parent, false)
        return BaseViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>?, position: Int) {
        holder?.bind(items[position], position)
    }

    override fun getItemCount() = items.size


    class BaseViewHolder<in T>(private val binding: ViewDataBinding,
        private val listener: OnItemListener<T>) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T, position: Int) {
            binding.setVariable(BR.item, item)
            binding.setVariable(BR.position, position)
            binding.setVariable(BR.listener, listener)
            binding.executePendingBindings()
        }
    }

    interface OnItemListener<in T> {
        fun onClick(item: T, position: Int)
    }

    interface OnItemLongListener<T> : OnItemListener<T> {
        fun onLongClick(item: T, position: T)
    }
}
