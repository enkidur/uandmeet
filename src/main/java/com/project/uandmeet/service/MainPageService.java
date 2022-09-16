package com.project.uandmeet.service;

import com.project.uandmeet.dto.MainPageDto;
import com.project.uandmeet.dto.MainPageEntryDto;
import com.project.uandmeet.dto.MainPageMatchingDto;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Category;
import com.project.uandmeet.model.Entry;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.BoardRepository;
import com.project.uandmeet.repository.CategoryRepository;
import com.project.uandmeet.repository.EntryRepository;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor //생성자 미리 생성.
public class MainPageService {
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final EntryRepository entryRepository;

    public List<MainPageDto> maininformation(String category) {
        List<MainPageDto> temp = new ArrayList<>();
        List<MainPageDto> mainPage = new ArrayList<>();
        if (category.equals("all")) {
            List<Board> mains = boardRepository.findByBoardTypeOrderByLikeCountDesc("information");
            for (Board main : mains) {
                MainPageDto mainPageDto = new MainPageDto(main.getId(),
                        main.getTitle(),
                        main.getContent(),
                        main.getMember().getNickname(),
                        main.getBoardimage(),
                        main.getLikeCount(),
                        main.getCommentCount());
                temp.add(mainPageDto);
            }
        } else {
            Category mainCategory = categoryRepository.findAllByCategory(category).orElseThrow(
                    () -> new CustomException(ErrorCode.BOARD_NOT_FOUND)
            );
            List<Board> mains = boardRepository.findByBoardTypeAndCategoryOrderByLikeCountDesc("information", mainCategory);
            for (Board main : mains) {
                MainPageDto mainPageDto = new MainPageDto(main.getId(),
                        main.getTitle(),
                        main.getContent(),
                        main.getMember().getNickname(),
                        main.getBoardimage(),
                        main.getLikeCount(),
                        main.getCommentCount());
                temp.add(mainPageDto);
            }
        }
        if (temp.size() < 6) {
            mainPage.addAll(temp);
        } else {
            for (int i = 0; i < 5; i++) {
                mainPage.add(temp.get(i));
            }
        }
        return mainPage;
    }

    public List<MainPageMatchingDto> mainmatching(String category) {
        List<MainPageMatchingDto> temp = new ArrayList<>();
        List<MainPageMatchingDto> mainPage = new ArrayList<>();
        if (category.equals("all")) {
            List<Board> mains = boardRepository.findByBoardTypeOrderByLikeCountDesc("matching");
            for (Board main : mains) {
                MainPageMatchingDto mainPageDto = new MainPageMatchingDto(main.getCategory().getCategory(),
                        main.getId(),
                        main.getTitle(),
                        main.getContent(),
                        main.getMember().getNickname(),
                        main.getEndDateAt(),
                        main.getCurrentEntry(),
                        main.getMaxEntry(),
                        main.getBoardimage(),
                        main.getLikeCount(),
                        main.getCommentCount());
                temp.add(mainPageDto);
            }
        } else {
            Category mainCategory = categoryRepository.findAllByCategory(category).orElseThrow(
                    () -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
            );
            List<Board> mains = boardRepository.findByBoardTypeAndCategoryOrderByLikeCountDesc("matching", mainCategory);
            for (Board main : mains) {
                MainPageMatchingDto mainPageDto = new MainPageMatchingDto(main.getCategory().getCategory(),
                        main.getId(),
                        main.getTitle(),
                        main.getContent(),
                        main.getMember().getNickname(),
                        main.getEndDateAt(),
                        main.getCurrentEntry(),
                        main.getMaxEntry(),
                        main.getBoardimage(),
                        main.getLikeCount(),
                        main.getCommentCount());
                temp.add(mainPageDto);
            }
        }
        if (temp.size() < 5) { // Page or Slice 로 해결가능
            mainPage.addAll(temp);
        } else {
            for (int i = 0; i < 4; i++) {
                mainPage.add(temp.get(i));
            }
        }
        return mainPage;
    }


    public List<MainPageEntryDto> mainmyentry(UserDetailsImpl userDetails) {
        Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        List<Entry> entries = entryRepository.findByMember(member);
        List<Board> boards = boardRepository.findByMemberAndBoardTypeOrderByCreatedAtDesc(member, "matching");
        List<MainPageEntryDto> temp = new ArrayList<>();
        List<MainPageEntryDto> boardInfo = new ArrayList<>();
        for (Entry entry : entries) {
            MainPageEntryDto responseDto = new MainPageEntryDto(
                    "entryPost",
                    entry.getBoard().getId(),
                    entry.getBoard().getTitle(),
                    entry.getBoard().getMaxEntry(),
                    entry.getBoard().getCurrentEntry(),
                    entry.isMatching(),
                    entry.getBoard().getBoardimage(),
                    entry.getBoard().getLikeCount(),
                    entry.getBoard().getCommentCount(),
                    entry.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                    entry.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
            temp.add(responseDto);
        }
        for (Board board : boards) {
            MainPageEntryDto responseDto = new MainPageEntryDto(
                    "myPost",
                    board.getId(),
                    board.getTitle(),
                    board.getMaxEntry(),
                    board.getCurrentEntry(),
                    null,
                    board.getBoardimage(),
                    board.getLikeCount(),
                    board.getCommentCount(),
                    board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                    board.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
            temp.add(responseDto);
        }
        Comparator<MainPageEntryDto> comparator = Comparator.comparing(prod -> String.valueOf(prod.getCreatedAt()));
        temp.sort(comparator);
        if (temp.size() < 5) {
            boardInfo.addAll(temp);
        } else {
            for (int i = 0; i < 4; i++) {
                boardInfo.add(temp.get(i));
            }
        }
        return boardInfo;
    }
}
