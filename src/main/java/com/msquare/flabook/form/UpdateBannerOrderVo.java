package com.msquare.flabook.form;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBannerOrderVo {

    private Integer[] rankOrder;
    private Long[] id;

}
