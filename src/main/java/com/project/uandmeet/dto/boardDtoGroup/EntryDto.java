package com.project.uandmeet.dto.boardDtoGroup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EntryDto {
    private Long boardid;
    private Boolean isMatching;
}
