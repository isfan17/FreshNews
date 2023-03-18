package com.isfan17.freshnews.helper

import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

object FragmentHelper {
    fun isFragmentVisible(fragment: WeakReference<Fragment>?): Boolean {
        return fragment?.get()?.let { frag ->
            frag.activity != null && frag.isVisible && !frag.isRemoving
        } ?: false
    }
}