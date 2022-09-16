package com.msquare.flabook.dto.mapper;

import com.msquare.flabook.dto.ReleaseNoteDto;
import com.msquare.flabook.models.ReleaseNote;
import com.msquare.flabook.util.VersionComparer;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReleaseNoteMapper {
    ReleaseNoteMapper INSTANCE = Mappers.getMapper( ReleaseNoteMapper.class );

    ReleaseNoteDto of(ReleaseNote releaseNote);
    ReleaseNoteDto of(VersionComparer.AppUpdateResult appUpdateResult);
}
