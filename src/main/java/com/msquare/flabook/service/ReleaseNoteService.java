package com.msquare.flabook.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.enumeration.OsType;
import com.msquare.flabook.models.ReleaseNote;
import com.msquare.flabook.repository.ReleaseNoteRepository;
import com.msquare.flabook.util.VersionComparer;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ReleaseNoteService {

    private final ReleaseNoteRepository releaseNoteRepository;

    public ReleaseNote findReleaseNote(OsType osType) {
        List<ReleaseNote> releaseNotes = releaseNoteRepository.findByOsTypeOrderByReleaseDateDesc(osType);
        if(!releaseNotes.isEmpty()) {
            return releaseNotes.get(0);
        }

        return null;
    }

    public VersionComparer.AppUpdateResult findReleaseNote(OsType osType, String version) {
        List<ReleaseNote> releaseNotes = releaseNoteRepository.findByOsTypeOrderByReleaseDateDesc(osType);
        if(!releaseNotes.isEmpty()) {
            ReleaseNote releaseNote = releaseNotes.get(0);
            VersionComparer.AppUpdateResult result;
            if(version != null && !version.isEmpty()) {
                result = VersionComparer.checkVersion(releaseNote, version);
            } else {
                result = new VersionComparer.AppUpdateResult(releaseNote.getVersion(), null, releaseNote.getForceUpdate());
            }
            return result;
        }
        return null;
    }



}
