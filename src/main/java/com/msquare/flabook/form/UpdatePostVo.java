package com.msquare.flabook.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.msquare.flabook.models.board.ClinicCondition;
import org.springframework.validation.annotation.Validated;

import java.util.List;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Validated
@ToString(callSuper = true)
public class UpdatePostVo extends BasePostVo {

    private String[] ids;
    private List<String> goodsNos;

    private ClinicCondition.ClinicConditionPlace place;
    private ClinicCondition.ClinicConditionLight light;
    private ClinicCondition.ClinicConditionWater water;
    private ClinicCondition.ClinicDetailRepotting repotting;

}
