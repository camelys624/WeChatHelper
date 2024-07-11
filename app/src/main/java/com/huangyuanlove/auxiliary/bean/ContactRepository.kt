package com.huangyuanlove.auxiliary.bean

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactRepository() {
    private lateinit var contactDAO: ContactDAO


    suspend fun insertContact(contact: Contact){
        withContext(Dispatchers.IO){
            contactDAO.insertContact(contact)
        }
    }
    suspend fun deleteContact(contact: Contact){
        withContext(Dispatchers.IO){
            contactDAO.deleteContact(contact)
        }
    }
}