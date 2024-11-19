package com.project.parkrental.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class CommunityController {

    @Autowired
    private CommunityService communityService;


    @Autowired
    private CommuLikeService commuLikeService; // 좋아요 서비스 주입

    // 커뮤니티 목록 페이지 (페이징 기능 추가)
    @GetMapping("/guest/communityList")
    public String listCommunities(Model model,
                                  @RequestParam(defaultValue = "0") int page,  // 기본 페이지 0
                                  @RequestParam(defaultValue = "10") int size,  // 기본 페이지 크기 10
                                  @RequestParam(required = false) String search, // 검색어 추가
                                  @RequestParam(required = false) String filter) { // 필터 추가
        // 검색어가 있을 경우 페이징 처리된 커뮤니티 목록을 가져옴
        Page<Community> communitiesPage;
        if (search != null && !search.isEmpty()) {
            if ("title".equals(filter)) {
                communitiesPage = communityService.searchCommunitiesByTitle(search, page, size); // 제목으로 검색
            } else if ("content".equals(filter)) {
                communitiesPage = communityService.searchCommunitiesByContent(search, page, size); // 내용으로 검색
            } else {
                communitiesPage = communityService.searchCommunities(search, page, size); // 제목 및 내용으로 검색
            }
        } else {
            communitiesPage = communityService.getCommunitiesPaged(page, size, null); // 전체 목록 가져오기
        }

        // 페이징된 커뮤니티 목록을 모델에 추가
        model.addAttribute("communitiesPage", communitiesPage);
        model.addAttribute("search", search); // 검색어 모델에 추가
        model.addAttribute("filter", filter); // 필터 모델에 추가

        return "guest/communityList";  // Thymeleaf 템플릿
    }



    // 커뮤니티 상세 페이지 (좋아요 기능 추가)
    @GetMapping("/user/communityDetail/{id}")
    public String viewCommunity(@PathVariable("id") Long id, Model model) {
        Community community = communityService.getCommunityById(id).orElse(null);
        if (community != null) {
            // Optionally, increment the view count
            community.setViewCount(community.getViewCount() + 1);
            communityService.save(community);
        }

        // 좋아요 개수 가져오기
        long likeCount = commuLikeService.getLikeCount(id);
        model.addAttribute("likeCount", likeCount); // 모델에 좋아요 개수 추가
        model.addAttribute("community", community);

        return "user/communityDetail";  // Make sure this file exists in the user folder
    }

    // 좋아요 기능 처리
    @PostMapping("/user/communityDetail/{id}/like")
    public String likePost(@PathVariable Long id) {
        // 현재 로그인한 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 좋아요 토글 처리
        commuLikeService.toggleLike(username, id);

        return "redirect:/user/communityDetail/" + id;  // 좋아요 처리 후 다시 게시글 상세보기 페이지로 리다이렉트
    }


    // 커뮤니티 작성 페이지
    @GetMapping("/user/communityWrite") // URL 매핑을 communityWrite로 변경
    public String showCreateForm(Model model) {
        model.addAttribute("community", new Community());
        return "user/communityWrite"; // user 폴더 내 뷰
    }

    @PostMapping("/guest/communityList")
    public String createCommunity(@ModelAttribute Community community,
                                  @RequestParam("inquiry_ofile") MultipartFile file) throws IOException {
        // 파일 업로드 처리
        if (!file.isEmpty()) {
            String originalFileName = file.getOriginalFilename(); // 원본 파일명
            String fileExtension = getFileExtension(originalFileName); // 파일 확장자 추출

            // 허용된 확장자 목록
            List<String> allowedExtensions = Arrays.asList("pdf", "zip", "rar", "ppt", "pptx", "png", "jpg", "jpeg", "gif");

            // 파일 확장자가 허용된 형식인지 확인
            if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
                throw new IOException("허용되지 않는 파일 형식입니다. PDF, ZIP, RAR, PPT 파일만 업로드 가능합니다.");
            }

            String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName; // 저장될 파일명
            String uploadPath = System.getProperty("user.home") + "/uploads/"; // 홈 디렉토리의 uploads 폴더

            File saveFile = new File(uploadPath, storedFileName);
            file.transferTo(saveFile); // 파일 저장

            // 파일 정보 저장
            community.setOfile(originalFileName); // 원본 파일명 저장
            community.setSfile(storedFileName);   // 서버에 저장된 파일명 저장

            // 서버의 URL을 설정
            String fileUrl = "http://localhost:8081/user/files/" + storedFileName;
            community.setFileUrl(fileUrl); // 새로운 필드에 URL 저장 (필드 추가 필요)
        }

        // 현재 로그인한 사용자로 작성자 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 현재 로그인한 사용자 이름 가져오기
        community.setUsername(username); // 작성자 설정

        community.setPostdate(LocalDateTime.now()); // 현재 날짜 및 시간 설정
        communityService.createCommunity(community); // 커뮤니티 저장
        return "redirect:/guest/communityList"; // 목록 페이지로 리다이렉트
    }

    // 파일 확장자 추출 메서드
    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.') + 1);
        }
        return "";
    }

    // 커뮤니티 수정 페이지
    @GetMapping("/user/communityEdit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Community community = communityService.getCommunityById(id).orElse(null);
        model.addAttribute("community", community);
        return "user/communityEdit"; // 수정된 경로 및 파일명
    }

    // 커뮤니티 수정 처리
    @PostMapping("/user/community/update/{idx}")
    public String updateCommunity(@PathVariable Long idx,
                                  @RequestParam String title,
                                  @RequestParam String content,
                                  @RequestParam(required = false) MultipartFile inquiry_ofile,
                                  @RequestParam(required = false) boolean deleteFile,
                                  RedirectAttributes redirectAttributes) {

        Community community = communityService.findById(idx);
        if (community != null) {
            community.setTitle(title);
            community.setContent(content);

            // Handle file deletion
            if (deleteFile) {
                // Add logic to delete the file from the server if necessary
                File existingFile = new File(System.getProperty("user.home") + "/uploads/" + community.getSfile());
                if (existingFile.exists()) {
                    existingFile.delete(); // Delete the file from the filesystem
                }
                community.setOfile(null); // Assuming you store the original file name in 'ofile'
                community.setSfile(null); // Assuming you store the secure file name in 'sfile'
            }

            // Handle file upload
            if (!inquiry_ofile.isEmpty()) {
                String fileName = inquiry_ofile.getOriginalFilename();
                String uploadPath = System.getProperty("user.home") + "/uploads/"; // Define the upload path
                File uploadFile = new File(uploadPath + fileName);

                try {
                    // Create directory if it does not exist
                    new File(uploadPath).mkdirs();
                    inquiry_ofile.transferTo(uploadFile); // Save the uploaded file
                    community.setOfile(fileName); // Save the uploaded file name
                    community.setSfile(fileName); // Store the filename for future reference
                } catch (IOException e) {
                    // Handle the exception
                    redirectAttributes.addFlashAttribute("error", "파일 업로드 중 오류가 발생했습니다.");
                }
            }

            communityService.saveCommunity(community);
            redirectAttributes.addFlashAttribute("message", "게시물이 수정되었습니다.");
        }

        return "redirect:/guest/communityList";
    }

    // 커뮤니티 삭제 처리
    @GetMapping("/user/community/delete/{id}")
    public String deleteCommunity(@PathVariable("id") Long id) {
        communityService.deleteCommunity(id);
        return "redirect:/guest/communityList";
    }

    @GetMapping("/user/files/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            // Set the upload path
            String uploadPath = System.getProperty("user.home") + "/uploads/";
            Path filePath = Paths.get(uploadPath).resolve(filename).normalize();

            // Create PathResource instead of UrlResource
            Resource resource = new PathResource(filePath);

            // Check if the file exists
            if (!resource.exists()) {
                throw new IOException("파일을 찾을 수 없습니다: " + filename);
            }

            // Determine the content type based on the file extension
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Default to binary stream
            }

            // Encode the filename for the Content-Disposition header
            String encodedFilename = URLEncoder.encode(resource.getFilename(), "UTF-8").replaceAll("\\+", "%20");

            // Return the file as a response
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
                    .body(resource);
        } catch (IOException e) {
            // Handle the exception
            return ResponseEntity.status(500).body(null);
        }
    }
}
