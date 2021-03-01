package com.example.mercadomunicipalcliente.data;



import com.example.mercadomunicipalcliente.models.Invoice;
import com.example.mercadomunicipalcliente.models.Store;
import com.example.mercadomunicipalcliente.models.User;

import java.util.List;

public class AppData {
    //Listas a las cuales se puede acceder desde cualquier parte del programa.
    public static List<Store> storeList;
    public static List<User> userList;
    public static List<Invoice> invoiceList;

    public static Store getStoreById(String storeID) {
        for (Store store : storeList) {
            if (store.ID.equals(storeID)) {
                return store;
            }
        }
        return null;
    }

    public static User getUserById(String uid) {
        for (User user : userList) {
            if (user.userID.equals(uid)) {
                return user;
            }
        }
        return null;
    }

    public static Invoice getInvoiceById(String uid) {
        for (Invoice invoice : invoiceList) {
            if (Integer.toString(invoice.number).equals(uid)) {
                return invoice;
            }
        }
        return null;
    }
}
