package org.example.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.models.Root;

import java.lang.reflect.Type;
import java.util.List;

public class JsonDecodeImpl implements JsonDecode{

    @Override
    public List<Root> JsonDe(String json) {
        try {
        Type type = new TypeToken<List<Root>>(){}.getType();
        Gson gson = new Gson();
        List<Root> roots = gson.fromJson(json.toString(), type);
        return roots;
        }catch (Exception e){
        System.out.println("error" + e.getMessage());
        }
        return null;
    }
}
