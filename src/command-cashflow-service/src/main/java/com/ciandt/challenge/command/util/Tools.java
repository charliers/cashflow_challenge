package com.ciandt.challenge.command.util;

import com.ciandt.challenge.shared.domain.FinancialRecord;
import com.google.gson.Gson;

public class Tools {

    public static String toJson(FinancialRecord record){
        return new Gson().toJson(record);
    }

    public static FinancialRecord toFinancialRecord( String json){
        return new Gson().fromJson(json, FinancialRecord.class);
    }
}
