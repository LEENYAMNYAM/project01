package com.example.pro.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadResultDTO {
    private String uuid;
    private String filename;
    private boolean image;

    public String getLink(){
        if(image){
            return "s_"+uuid + "_" + filename;
        }else{
            return uuid + "_" + filename;
        }
    }

}
