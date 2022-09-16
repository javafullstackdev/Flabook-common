package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.msquare.flabook.dto.mapper.AddressMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.UserEventInfo;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.BaseView.class)
public class ShippingDto implements Serializable {

    private String name;
    private String roadAddress;
    private String postCode;
    private String detailAddress;
    private String phone1;
    private String phone2;

    public static ShippingDto of(UserEventInfo userEventInfo) {
        return AddressMapper.INSTANCE.of(userEventInfo);
    }
}
