package com.msquare.flabook.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import com.msquare.flabook.dto.mapper.ClinicConditionMapper;
import com.msquare.flabook.json.Views;
import com.msquare.flabook.models.board.ClinicCondition;

import java.io.Serializable;

@JsonView({Views.BaseView.class})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicConditionDto implements Serializable {

    @NonNull
    private ClinicCondition.ClinicConditionPlace place;

    @NonNull
    private ClinicCondition.ClinicConditionLight light;

    @NonNull
    private ClinicCondition.ClinicConditionWater water;

    @NonNull
    private ClinicCondition.ClinicDetailRepotting repotting;

    public static ClinicConditionDto of(ClinicCondition clinicCondition) {
        return ClinicConditionMapper.INSTANCE.of(clinicCondition);
    }

}
