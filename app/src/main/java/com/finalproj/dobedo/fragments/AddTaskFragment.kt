package com.finalproj.dobedo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.finalproj.dobedo.databinding.FragmentAddTaskBinding
import com.finalproj.dobedo.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText

class AddTaskFragment : DialogFragment() {

    // Initialize view binding and listener
    private lateinit var binding: FragmentAddTaskBinding
    private lateinit var listener: DialogNxtBtnCL
    private var toDoData: ToDoData? = null

    // Set the listener for this fragment
    fun setListener(listener: HomeFragment) {
        this.listener = listener
    }

    // Companion object to create a new instance of AddTaskFragment
    companion object {
        const val TAG = "AddTaskFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String) = AddTaskFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check for arguments and populate UI elements if available
        if (arguments != null) {
            toDoData = ToDoData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString()
            )
            binding.todo.setText(toDoData?.task)
        }

        // Register click events for buttons
        registerEvents()
    }

    // Register click events for buttons
    private fun registerEvents() {
        binding.btnAdd.setOnClickListener {
            val toDo = binding.todo.text.toString()

            if (toDo.isNotEmpty()) {
                if (toDoData == null) {
                    // Call the listener method to save a new task
                    listener.onSaveTask(toDo, binding.todo)
                } else {
                    // Call the listener method to update an existing task
                    toDoData?.task = toDo
                    listener.onUpdateTask(toDoData!!, binding.todo)
                }
            } else {
                // Show a toast message if the task content is empty
                Toast.makeText(context, "Please add some content", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnClose.setOnClickListener {
            // Dismiss the dialog fragment
            dismiss()
        }
    }

    // Interface to define callback methods for button clicks
    interface DialogNxtBtnCL {
        fun onSaveTask(todoTask: String, todo: TextInputEditText)
        fun onUpdateTask(toDoData: ToDoData, todo: TextInputEditText)
    }
}
