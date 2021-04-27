package com.ezzy.projectmanagement.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

enum class Directions{
    VERTICAL,
    HORIZONTAL
}

class ItemDecorator<T> (
    private val directionType : T,
    private val space : Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        when(directionType) {
            Directions.VERTICAL -> {
                outRect.bottom = space
                if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)){
                    outRect.bottom = space
                }
            }
            Directions.HORIZONTAL -> {
                outRect.right = space
                if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)){
                    outRect.right = space
                }
            }
        }

    }
}