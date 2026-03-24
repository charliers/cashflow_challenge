package com.ciandt.challenge.command.util;

import com.ciandt.challenge.shared.domain.FinancialRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;

public class Tools {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar objeto para JSON", e);
        }
    }

    public static FinancialRecord toFinancialRecord( String json){
        return new Gson().fromJson(json, FinancialRecord.class);
    }
}
