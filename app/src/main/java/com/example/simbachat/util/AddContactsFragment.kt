
package com.example.simbachat.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.simbachat.R
import com.example.simbachat.dashboard.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class AddContactsFragment : Fragment(R.layout.fragment_add_contacts) {

    private lateinit var searchContacts: EditText
    private lateinit var contactsStatus: TextView
    private lateinit var contactsList: LinearLayout

    private val db by lazy { FirebaseFirestore.getInstance() }

    private data class LocalContact(
        val name: String,
        val number: String,
        val e164: String
    )

    private val localContacts = mutableListOf<LocalContact>()
    private val registeredUsers = linkedMapOf<String, Map<String, Any>>()

    private var searchToken = 0

    private val requestContactsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                loadPhoneContacts()
            } else {
                contactsStatus.text =
                    "Contact access denied. You can still search manually by number."
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchContacts = view.findViewById(R.id.searchContacts)
        contactsStatus = view.findViewById(R.id.contactsStatus)
        contactsList = view.findViewById(R.id.contactsList)

        searchContacts.doAfterTextChanged { editable ->
            val query = editable?.toString().orEmpty()

            displayRegisteredContacts(query)

            if (query.filter { it.isDigit() }.length >= 7) {
                searchManualNumber(query)
            }
        }

        if (
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadPhoneContacts()
        } else {
            contactsStatus.text =
                "Allow contact access to find registered SimbaChat users, or search manually."

            requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun loadPhoneContacts() {

        localContacts.clear()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        try {
            val cursor: Cursor? = requireContext().contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )

            cursor?.use {

                val nameIndex = it.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                )

                val numberIndex = it.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )

                while (it.moveToNext()) {

                    val name = it.getString(nameIndex)?.trim().orEmpty()
                    val number = it.getString(numberIndex)?.trim().orEmpty()

                    val normalized =
                        PhoneNumberUtils.toE164(number) ?: continue

                    if (name.isNotBlank()) {
                        localContacts.add(
                            LocalContact(name, number, normalized)
                        )
                    }
                }
            }

            lookupPhoneContacts()

        } catch (e: SecurityException) {
            contactsStatus.text = "Contact permission is required."
        } catch (e: Exception) {
            contactsStatus.text = "Unable to read phone contacts."
        }
    }


    private fun lookupPhoneContacts() {

        val numbers = localContacts
            .map { it.e164.filter { digit -> digit.isDigit() } }
            .distinct()

        registeredUsers.clear()

        if (numbers.isEmpty()) {
            contactsStatus.text = "No phone numbers found."
            displayRegisteredContacts("")
            return
        }

        contactsStatus.text = "Finding SimbaChat users..."

        var completed = 0

        numbers.forEach { number ->

            db.collection("phone_directory")
                .document(number)
                .get()
                .addOnSuccessListener { document ->

                    val data = document.data
                    val uid = data?.get("uid") as? String

                    if (!uid.isNullOrBlank() && data != null) {
                        registeredUsers[uid] = data
                    }

                    completed++

                    if (completed == numbers.size && isAdded) {
                        contactsStatus.text =
                            "${registeredUsers.size} registered SimbaChat users found."

                        displayRegisteredContacts("")
                    }
                }
                .addOnFailureListener {

                    completed++

                    if (completed == numbers.size && isAdded) {
                        contactsStatus.text =
                            "Contact search completed. Some numbers could not be checked."

                        displayRegisteredContacts("")
                    }
                }
        }
    }


    private fun searchManualNumber(raw: String) {

        val normalized = PhoneNumberUtils.toE164(raw) ?: return
        val token = ++searchToken

        val documentId = normalized.filter { it.isDigit() }

        db.collection("phone_directory")
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->

                if (!isAdded || token != searchToken) return@addOnSuccessListener

                val data = document.data
                val uid = data?.get("uid") as? String

                if (!uid.isNullOrBlank() && data != null) {
                    registeredUsers[uid] = data
                }

                displayRegisteredContacts(raw)

                if (uid.isNullOrBlank()) {
                    contactsStatus.text =
                        "No registered SimbaChat account found for this number."
                }
            }
            .addOnFailureListener {

                if (isAdded && token == searchToken) {
                    contactsStatus.text = "Number search failed. Check your connection."
                }
            }
    }

    private fun displayRegisteredContacts(query: String) {

        if (!isAdded) return

        contactsList.removeAllViews()

        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        val q = query.trim()

        val matches = registeredUsers.filter { (uid, data) ->

            uid != currentUid &&
                    (
                            q.isBlank() ||
                                    (data["name"] as? String).orEmpty().contains(q, true) ||
                                    (data["phone_number"] as? String).orEmpty().contains(q, true) ||
                                    localContacts.any {
                                        it.e164.filter { ch -> ch.isDigit() } ==
                                                (data["phone_number_e164"] as? String)
                                                    ?.filter { ch -> ch.isDigit() } &&
                                                it.name.contains(q, true)
                                    }
                            )
        }

        if (matches.isEmpty()) {
            addMessage(
                if (q.isBlank()) {
                    "Registered SimbaChat users from your phonebook will appear here."
                } else {
                    "No registered SimbaChat users found."
                }
            )
            return
        }

        matches.forEach { (uid, data) ->
            addUserRow(uid, data)
        }
    }

    private fun addMessage(message: String) {

        contactsList.addView(
            TextView(requireContext()).apply {
                text = message
                setTextColor(Color.LTGRAY)
                textSize = 14f
                gravity = Gravity.CENTER
                setPadding(12, 32, 12, 32)
            }
        )
    }

    private fun addUserRow(uid: String, data: Map<String, Any>) {

        val name = (data["name"] as? String)
            .orEmpty()
            .ifBlank { "SimbaChat user" }

        val number = (data["phone_number"] as? String).orEmpty()

        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(4, 12, 4, 12)

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val avatar = TextView(requireContext()).apply {
            text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            textSize = 20f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#C9A84C"))

            layoutParams = LinearLayout.LayoutParams(46, 46).apply {
                marginEnd = 12
            }
        }

        val details = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL

            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        details.addView(
            TextView(requireContext()).apply {
                text = name
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.WHITE)
            }
        )

        details.addView(
            TextView(requireContext()).apply {
                text = number
                textSize = 13f
                setTextColor(Color.LTGRAY)
            }
        )

        val addButton = Button(requireContext()).apply {
            text = "Add"
            isAllCaps = false

            setOnClickListener {
                addContactAndOpenChat(uid, name)
            }
        }

        row.addView(avatar)
        row.addView(details)
        row.addView(addButton)

        contactsList.addView(row)

        contactsList.addView(
            View(requireContext()).apply {
                setBackgroundColor(Color.parseColor("#292929"))

                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1
                )
            }
        )
    }

    private fun addContactAndOpenChat(
        otherUid: String,
        displayName: String
    ) {

        val currentUid =
            FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (currentUid == otherUid) return

        val contactData = hashMapOf(
            "uid" to otherUid,
            "name" to displayName,
            "addedAt" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(currentUid)
            .collection("contacts")
            .document(otherUid)
            .set(contactData)
            .addOnSuccessListener {

                if (!isAdded) return@addOnSuccessListener

                Toast.makeText(
                    requireContext(),
                    "$displayName added successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(requireContext(), Chat::class.java)
                intent.putExtra("receiverId", otherUid)

                startActivity(intent)
            }
            .addOnFailureListener { error ->

                if (isAdded) {
                    Toast.makeText(
                        requireContext(),
                        "Could not add contact: ${error.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
