package com.wepos.catalog;

import com.wepos.authentication.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CatalogService {
    @Autowired
    private AuthenticationService authenticationService;

    public void getCatalog(){

        Map<String,String> params = new HashMap<>();
        params.put("current",String.valueOf(1));
        params.put("rowCount",String.valueOf(100));
        params.put("lang","EN");
        params.put("current",String.valueOf(1));
        authenticationService.getCatalog(params);
    }

}
