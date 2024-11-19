package com.project.parkrental.controller;

import com.project.parkrental.inquiryBoard.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class InquiryController {

    @Autowired
    InquiryService inquiryService;

    //전체 목록 보기
    @GetMapping("/guest/inquiryList")
    public String inquiryList(@PageableDefault(size = 10, sort = "postdate", direction = Sort.Direction.DESC) Pageable pageable,
                              @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "filter", required = false) String filter,
                              Model model) {
        Page<InquiryResponseDTO> inquiryResponseDTOs;
        if (keyword != null && !keyword.trim().isEmpty()) {
            if ("제목만".equals(filter)) {
                System.out.println("제목으로 검색: " + keyword);
                inquiryResponseDTOs = inquiryService.searchInquiriesByTitle(keyword, pageable);
            } else if ("내용만".equals(filter)) {
                inquiryResponseDTOs = inquiryService.searchInquiriesByContent(keyword, pageable);
            } else if ("작성자".equals(filter)) {
                inquiryResponseDTOs = inquiryService.searchInquiriesByUsername(keyword, pageable);
            } else {
                inquiryResponseDTOs = inquiryService.findAll(pageable); // 필터가 설정되지 않은 경우
            }
        } else {
            inquiryResponseDTOs = inquiryService.findAll(pageable);
        }

        model.addAttribute("inquiries", inquiryResponseDTOs);
        model.addAttribute("keyword", keyword); // 검색어를 모델에 추가
        model.addAttribute("filter", filter); // 선택된 필터를 모델에 추가
        return "guest/inquiryList"; // Thymeleaf 템플릿 이름
    }


    //비밀번호 확인창
    @RequestMapping("/user/inquiryPass")
    public String inquiryPass(HttpServletRequest request, Model model){
        String sId = SecurityContextHolder.getContext().getAuthentication().getName();
        Long idx = Long.valueOf(request.getParameter("idx"));
        Inquiry inquiry = inquiryService.inquiryView(idx);

        // 사용자 권한 확인
        if (inquiry != null) {
            // admin 권한 확인
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                // admin 권한이면 바로 상세 뷰로 이동
                inquiryService.inquiryUpdateViewCount(idx);
                model.addAttribute("inquiries", inquiry);
                model.addAttribute("isImageFile", inquiryService.isImageFile(inquiry.getSfile()));
                return "redirect:/user/inquiryView?idx=" + idx;
            }else if (inquiry.getUsername().equals(sId) || inquiryService.isAdminResponse(idx)) {
                // 일반 사용자가 자신이 쓴 글을 조회할 경우
                model.addAttribute("inquiry", inquiry);
                model.addAttribute("Id", sId);
            } else {
                // 일반 사용자가 자신이 쓴 글이 아닐 경우
                model.addAttribute("message", "권한이 없습니다.");
                model.addAttribute("searchUrl", "/guest/inquiryList");
                return "guest/message"; // 메시지를 띄울 페이지
            }
        }

        return "user/inquiryPass";
    }

    //바말번호 확인!
    @PostMapping("/user/inquiryViewPro")
    @ResponseBody
    public Map<String, Object> inquiryPassPro(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        String password = request.getParameter("password");
        Long idx = Long.valueOf(request.getParameter("idx"));
        Inquiry inquiry = inquiryService.inquiryView(idx);

        if (inquiry != null && inquiry.getInquiryPassword().equals(password)) {
            inquiryService.inquiryUpdateViewCount(idx);
            response.put("success", true);
            response.put("idx", idx);
        } else {
            response.put("success", false);
            response.put("error", "비밀번호가 맞지 않습니다.");
        }

        return response; // JSON 형식으로 반환
    }


    //상세 뷰
    @RequestMapping("/user/inquiryView")
    public String inquiryBoardView(HttpServletRequest request, Model model) {

        String sId = SecurityContextHolder.getContext().getAuthentication().getName();
        Long idx = Long.valueOf(request.getParameter("idx"));
        Inquiry inquiry = inquiryService.inquiryView(idx);
        model.addAttribute("inquiries", inquiry);
        model.addAttribute("isImageFile", inquiryService.isImageFile(inquiry.getSfile()));

        return "user/inquiryView";
    }

    //글쓰기 란
    @GetMapping("/user/inquiryWrite")
    public String inquiryWriteForm(Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 현재 로그인한 사용자 이름
        model.addAttribute("username", username);
        model.addAttribute("inquiryCreate", new InquiryRequestDTO());
        return "user/inquiryWrite";
    }

    //수정 란
    @GetMapping("/user/inquiryEdit/{idx}")
    public String inquiryEditForm(Model model, @PathVariable("idx") Long idx){

        Inquiry updatePost = inquiryService.inquiryView(idx);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 현재 로그인한 사용자 이름

        model.addAttribute("username", username);
        model.addAttribute("inquiryRequest", updatePost);

        return "user/inquiryEdit";
    }

    //수정하기
    @PostMapping("/user/inquiryEditPro")
    public String inquiryEditPro(@RequestParam("idx") Long idx,
                                 @ModelAttribute("inquiryRequest") @Valid InquiryRequestDTO inquiryRequest,
                                 BindingResult bindingResult,
                                 @RequestPart(value = "file", required = false) MultipartFile file,
                                 HttpServletRequest request,
                                 Model model){

        if (bindingResult.hasErrors()){
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "user/inquiryEdit"; // 오류 발생 시 폼으로 돌아감
        }

        try {
            // 비밀번호가 입력되었으면 그 값을 사용, 아니면 기존 비밀번호 유지
            String newPassword = inquiryRequest.getInquiryPassword();
            if (newPassword != null && !newPassword.isEmpty()) {
                // 비밀번호가 입력되었을 때만 비밀번호를 변경
                inquiryRequest.setInquiryPassword(newPassword);
            } else {
                // 비밀번호가 입력되지 않으면 기존 비밀번호 유지
                inquiryRequest.setInquiryPassword(null);  // null로 설정하면 기존 비밀번호 유지
            }

            inquiryService.inquiryUpdate(idx, inquiryRequest, file);
            model.addAttribute("message", "게시글이 성공적으로 업데이트되었습니다.");
            model.addAttribute("searchUrl", "/guest/inquiryList");
        } catch (EntityNotFoundException e) {
            model.addAttribute("message", e.getMessage());
            return "user/inquiryEdit"; // 해당 게시글이 없을 경우 다시 폼으로
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "게시글 업데이트 중 오류가 발생했습니다.");
            return "user/inquiryEdit"; // 오류 발생 시 폼으로 돌아감
        }

        return "guest/message"; // 목록 페이지로 리다이렉트
    }


    //글쓰기
    @PostMapping("/user/inquiryWritePro")
    public String inquiryWritePro(@ModelAttribute("inquiryCreate") @Validated(WriteGroup.class) InquiryRequestDTO inquiryCreate, BindingResult bindingResult,
                                  @RequestPart(value="file", required = false) MultipartFile file,
                                  HttpServletRequest request, Model model) throws FileNotFoundException {

        if (bindingResult.hasErrors()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("username", username);
            model.addAttribute("inquiryCreate", inquiryCreate);
            return "user/inquiryWrite"; // 오류 발생 시 폼으로 돌아감
        }

        try {

            if (file != null && !file.isEmpty()) {
                String ofile = file.getOriginalFilename();
                String uploadDir = System.getProperty("user.dir") + "/ParkRentalProject/parkrental/src/main/resources/static/files";// 이미지 저장 경로 지정
                System.out.println(uploadDir);

                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                String sfile = UUID.randomUUID().toString() + "_" + ofile;

                File destination = new File(dir, sfile);
                file.transferTo(destination);

                // 파일이 있을 경우에만 DTO에 이미지 설정
                inquiryCreate.setOfile(ofile);
                inquiryCreate.setSfile(sfile);
            } else {
                // 파일이 없을 때 처리할 내용 (필요한 경우)
                System.out.println("No file uploaded.");
            }

            inquiryService.inquiryWrite(inquiryCreate, request); // inquiryWrite 호출
            model.addAttribute("message", "글 작성이 완료되었습니다.");
            model.addAttribute("searchUrl", "/guest/inquiryList");


        } catch (IOException | ServletException e) {
            e.printStackTrace();
            model.addAttribute("message", "파일 업로드 중 오류가 발생했습니다.");
            return "user/inquiryWrite";
        }

        return "guest/message"; // 목록 페이지로 리다이렉트
    }

    //글 삭제하기
    @GetMapping("/user/inquiryDelete/{idx}")
    @ResponseBody
    public Map<String, String> inquiryDelete(@PathVariable("idx") Long idx){
        inquiryService.inquiryDelete(idx);

        Map<String, String> response = new HashMap<>();
        response.put("message", "글이 성공적으로 삭제되었습니다.");
        response.put("searchUrl", "/guest/inquiryList");

        return response; // Map을 JSON으로 변환하여 반환
    }

    //답글 란
    @GetMapping("/admin/inquiryReply/{idx}")
    public String inquiryReplyForm(Model model, @PathVariable("idx") Long idx){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 현재 로그인한 사용자 이름
        String username = authentication.getName();
        model.addAttribute("username", username);
        Inquiry replyPost = inquiryService.inquiryView(idx);
        model.addAttribute("inquiry", replyPost);
        return "admin/inquiryReply";
    }

    //답글 작성
    @PostMapping("/admin/inquiryReplyPro/{idx}")
    public String inquiryReply(@PathVariable Long idx, @ModelAttribute("inquiry") @Valid InquiryRequestDTO inquiryReply,
                               @RequestParam MultipartFile file){
        Long inquiryIdx = inquiryReply.getIdx();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 현재 로그인한 사용자 이름
        inquiryReply.setUsername(username); // DTO에 사용자 이름 설정

        try {
            // 답글 작성 서비스 호출
            inquiryService.inquiryReplyWrite(inquiryReply, inquiryIdx != null ? inquiryIdx : idx, file);
            return "redirect:/user/inquiryView?idx=" + inquiryIdx;
        } catch (Exception e) {
            // 예외 처리 로직
            e.printStackTrace();
            return "admin/inquiryReply";
        }

    }

}
