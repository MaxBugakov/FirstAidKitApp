package com.example.myapplication2;


import android.util.Log;

import java.util.List;

public class DataBase {
    public static List<FamilyMember> getFamilyMembers() {
        List<FamilyMember> familyMembers = FamilyMember.listAll(FamilyMember.class);
        return familyMembers;
    }

    public static User getUser() {
        List<User> user = User.listAll(User.class);
        if (user.size() == 0)
            return null;
        return user.get(0);
    }
}
