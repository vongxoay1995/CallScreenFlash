package com.call.colorscreen.ledflash.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VB : ViewDataBinding> : Fragment() {
    private var _binding : VB? = null
    val binding :VB get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    abstract fun getLayoutRes(): Int

    abstract fun init()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (getLayoutRes() != 0) {
           _binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
            _binding!!.setLifecycleOwner { lifecycle}
            return  _binding!!.root
        } else {
            throw IllegalArgumentException("layout resource cannot be null")
        }
    }


}