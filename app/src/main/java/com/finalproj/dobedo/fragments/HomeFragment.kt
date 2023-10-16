package com.finalproj.dobedo.fragments

import android.graphics.drawable.AnimatedImageDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.finalproj.dobedo.R
import com.finalproj.dobedo.databinding.ActivityMainBinding
import com.finalproj.dobedo.databinding.FragmentHomeBinding
import com.finalproj.dobedo.utils.ToDoAdapter
import com.finalproj.dobedo.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), AddTaskFragment.DialogNxtBtnCL,
    ToDoAdapter.ToDoAdapterClicksInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ToDoAdapter
    private lateinit var mylist: MutableList<ToDoData>
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mainBinding: ActivityMainBinding
    private var popUpFragment: AddTaskFragment?=null

    @RequiresApi(Build.VERSION_CODES.P)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Load the image using a thread
        //GIF implementation
        Thread {
            val drawable = resources.getDrawable(R.drawable.dobedobestgirl, null)
            binding.gifGirl.post {
                binding.gifGirl.setImageDrawable(drawable)
                (drawable as? AnimatedImageDrawable)?.start()
            }
        }.start()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)
        init(view)
        getDataInFirebase()
        registerEvents()
    }

    // Register click events for buttons
    private fun registerEvents() {
        binding.btnAddTask.setOnClickListener {
            if (popUpFragment != null) {
                childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            }
            popUpFragment = AddTaskFragment()
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(
                childFragmentManager,
                AddTaskFragment.TAG
            )
        }


    }


    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
            .child("Tasks").child(auth.currentUser?.uid.toString())

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager=LinearLayoutManager(context)
        mylist= mutableListOf()
        adapter= ToDoAdapter(mylist)
        adapter.setListener(this)
        binding.recyclerView.adapter= adapter

        //Music Implementation
        //set to loop, so it will keep playing the music automatically when fragment is called.
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.beepbooptrack)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        setUpBGMusic() //Call BG Function


        //For Log out
        binding.btnExit.setOnClickListener {
            auth.signOut()
            val navController = Navigation.findNavController(requireView())
            navController.navigate(R.id.action_homeFragment_to_loginFragment)
        }


    }

    //Firebase Implementation
    private fun getDataInFirebase(){
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mylist.clear()
                for (tskSnapshot in snapshot.children) {
                    val taskId = tskSnapshot.key
                    val taskValue = tskSnapshot.value.toString()

                    val todoTask = ToDoData(taskId ?: "", taskValue)
                    mylist.add(todoTask)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setUpBGMusic(){
        binding.btnBgm.setOnClickListener {
            if (!this::mediaPlayer.isInitialized){
                mediaPlayer= MediaPlayer.create(requireContext(),R.raw.beepbooptrack)
            }
            if (mediaPlayer.isPlaying){
                mediaPlayer.pause()
                mediaPlayer.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayer.start()
        }

    }


    override fun onSaveTask(
        todoTask: String,
        todo: TextInputEditText,

    ) {
        databaseReference.push().setValue(todoTask).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "To Do Task Saved Successfully!", Toast.LENGTH_SHORT).show()

            } else {
                // Show a toast message if the task content is empty
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            todo.text = null
            popUpFragment!!.dismiss()
        }
    }

    override fun onUpdateTask(
        toDoData: ToDoData,
        todo: TextInputEditText,
    ) {
       val map= HashMap<String, Any>()
        map [toDoData.taskId]=toDoData.task
        databaseReference.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show()

            }else{
                // Show a toast message if the task content is empty
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            todo.text=null
            popUpFragment!!.dismiss()
        }
    }

    override fun onDltTaskBtnClicked(toDoData: ToDoData) {
       databaseReference.child(toDoData.taskId).removeValue().addOnCompleteListener {
           if (it.isSuccessful){
               Toast.makeText(context, "Deleted Successfully!", Toast.LENGTH_SHORT).show()
           }else{
               // Show a toast message if the task content is empty
               Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
           }
       }
    }

    override fun onEditTaskBtnClicked(toDoData: ToDoData) {
        if (popUpFragment != null) {
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
        }

        // Create a new instance of the AddTaskFragment with the data to edit
        popUpFragment = AddTaskFragment.newInstance(toDoData.taskId, toDoData.task)
        popUpFragment!!.setListener(this)

        // Show the fragment using the childFragmentManager
        popUpFragment!!.show(childFragmentManager, AddTaskFragment.TAG)
    }

    //Stop Music
    override fun onDestroy() {
        if (this::mediaPlayer.isInitialized){
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        super.onDestroy()
    }

}
