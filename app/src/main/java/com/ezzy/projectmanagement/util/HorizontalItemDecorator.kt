package com.ezzy.projectmanagement.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalItemDecorator(
    val horizontalSpaceWidth : Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.right = horizontalSpaceWidth
        if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)){
            outRect.right = horizontalSpaceWidth
        }
    }
}