package com.ezzy.projectmanagement.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalItemDecorator(
    val verticalSpaceHeight : Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = verticalSpaceHeight
        if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)){
            outRect.bottom = verticalSpaceHeight
        }
    }
}