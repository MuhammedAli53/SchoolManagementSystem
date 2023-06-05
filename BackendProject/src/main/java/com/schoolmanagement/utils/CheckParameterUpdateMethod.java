package com.schoolmanagement.utils;

import com.schoolmanagement.entity.abstracts.User;
import com.schoolmanagement.payload.request.DeanRequest;
import com.schoolmanagement.payload.request.abstracts.BaseUserRequest;

public class CheckParameterUpdateMethod {

    public static boolean checkParameter(User user, BaseUserRequest baseUserRequest){
        // unique datalari, orjinal datalarla ayni mi diye kontrol edicez. Bu methodu dean icin teacher icin vs her yerden kullanicaz. Bu nedenle DeanRequest olarak yazmadik,

        return user.getUsername().equalsIgnoreCase(baseUserRequest.getUsername()) ||
                user.getSsn().equalsIgnoreCase(baseUserRequest.getSsn()) ||
                user.getPhoneNumber().equalsIgnoreCase(baseUserRequest.getPhoneNumber());
                 // check edilecek.
    }


}
