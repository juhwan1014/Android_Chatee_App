package com.example.androidchateeinclass

import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidchateeinclass.databinding.ActivityChatBinding
import com.example.androidchateeinclass.databinding.ItemMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var messagesDB: DatabaseReference
    var messages: MutableList<Message> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        messagesDB = FirebaseDatabase.getInstance().getReference("Messages")

        binding.sendMessageButton.setOnClickListener {
            val sender = FirebaseAuth.getInstance().currentUser?.email
            val message = binding.messageInput.text.toString()
            if(sender != null){
                saveMessage(sender, message)

            }

        }

        // according to the docs, this will fire once when attached and subsequently onDataChange
        messagesDB.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messages = mutableListOf()
                dataSnapshot.children.forEach {
                    val message = it.getValue(Message::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }
                update()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read values: handle error
            }
        })


        // single column layout
        binding.messageList.layoutManager = LinearLayoutManager(this)
        // pass messages to the adapter
        binding.messageList.adapter = MessagesAdapter(messages)

    }

    private fun saveMessage(sender:String, messageBody: String){
        val key = messagesDB.push().key
        key ?: return
        val message = Message(sender,messageBody)
        messagesDB.child(key).setValue(message)
    }

    // update method to be called when UI needs to be refreshed
    private fun update(){
        // pass messages to the adapter
        binding.messageList.adapter = MessagesAdapter(messages)
    }

}

//val messages = listOf(
//    Message("someguy@example.com", "Oh hai! Blah blah blah blah blahblahblah."),
//    Message("someotherguy@example.com", "Yaya, blee blee bleeee.")
//)

private class MessagesAdapter(val messages: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class MessagesViewHolder(var binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        // need to explicitly cast the RecyclerView.ViewHolder as a MessagesViewHolder
        val messageHolder = holder as MessagesViewHolder
        // now we have access to each view element of the ViewHolder by id
        messageHolder.binding.senderLabel.text =  message.sender
        messageHolder.binding.messageBodyLabel.text = message.messageBody

        if (FirebaseAuth.getInstance().currentUser?.email == message.sender) {
            messageHolder.binding.senderImage.setImageResource(R.drawable.smile)
            messageHolder.binding.messageContainer.setBackgroundResource(R.drawable.rounded_background1)
        } else {
            messageHolder.binding.senderImage.setImageResource(R.drawable.stars)
            messageHolder.binding.messageContainer.setBackgroundResource(R.drawable.rounded_background2)
        }


    }

    override fun getItemCount(): Int {
        return messages.count()
    }
}


data class Message(var sender: String = "", var messageBody: String = "")
