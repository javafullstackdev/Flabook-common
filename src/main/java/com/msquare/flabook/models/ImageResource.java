package com.msquare.flabook.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import com.msquare.flabook.json.Views;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@JsonView(Views.BaseView.class)
public class ImageResource implements Serializable {

    public ImageResource(@NonNull String filekey, @NonNull String filename) {
        this.filekey = filekey;
        this.filename = filename;
    }

    @NonNull
    private String filekey;
    @NonNull
    private String filename;
}
