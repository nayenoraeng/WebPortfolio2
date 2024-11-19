package com.project.parkrental.noticeBoard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.print.Pageable;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    // 공지사항 목록 페이지 (페이징 기능 추가)
    @GetMapping("/guest/noticeList")
    public String listAnnouncements(Model model,
                                    @RequestParam(defaultValue = "0") int page,  // 기본 페이지 0
                                    @RequestParam(defaultValue = "10") int size,  // 기본 페이지 크기 10
                                    @RequestParam(required = false) String search, // 검색어 추가
                                    @RequestParam(required = false) String filter) { // 필터 추가
        // 검색어가 있을 경우 페이징 처리된 공지사항 목록을 가져옴
        Page<Announcement> announcementsPage;
        if (search != null && !search.isEmpty()) {
            if ("title".equals(filter)) {
                announcementsPage = announcementService.searchAnnouncementsByTitle(search, page, size); // 제목으로 검색
            } else if ("content".equals(filter)) {
                announcementsPage = announcementService.searchAnnouncementsByContent(search, page, size); // 내용으로 검색
            } else {
                announcementsPage = announcementService.searchAnnouncements(search, page, size); // 제목 및 내용으로 검색
            }
        } else {
            announcementsPage = announcementService.getAnnouncementsPaged(page, size, null); // 전체 목록 가져오기
        }

        // 페이징된 공지사항 목록을 모델에 추가
        model.addAttribute("announcementsPage", announcementsPage);
        model.addAttribute("search", search); // 검색어 모델에 추가
        model.addAttribute("filter", filter); // 필터 모델에 추가

        return "guest/noticeList";  // Thymeleaf 템플릿
    }


    // 공지사항 상세 페이지
    @GetMapping("/user/noticeDetail/{id}")
    public String viewAnnouncement(@PathVariable("id") Long id, Model model) {
        Announcement announcement = announcementService.getAnnouncementById(id).orElse(null);
        if (announcement != null) {
            // Optionally, increment the view count
            announcement.setViewCount(announcement.getViewCount() + 1);
            announcementService.save(announcement);
        }
        model.addAttribute("announcement", announcement);
        return "user/noticeDetail";  // Make sure this file exists in the user folder
    }

    // 공지사항 작성 페이지 (user/noticeWrite 경로)
    @GetMapping("/admin/noticeWrite") // URL 매핑을 noticeWrite로 변경
    public String showCreateForm(Model model) {
        model.addAttribute("announcement", new Announcement());
        return "admin/noticeWrite"; // user 폴더 내 뷰
    }

    @PostMapping("/guest/noticeList")
    public String createAnnouncement1(@ModelAttribute Announcement announcement,
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
            announcement.setOfile(originalFileName); // 원본 파일명 저장
            announcement.setSfile(storedFileName);   // 서버에 저장된 파일명 저장

            // 서버의 URL을 설정
            String fileUrl = "http://localhost:8081/admin/files/" + storedFileName;
            announcement.setFileUrl(fileUrl); // 새로운 필드에 URL 저장 (필드 추가 필요)
        }

        announcement.setUsername("admin"); // 작성자 설정
        announcement.setPostdate(LocalDateTime.now()); // 현재 날짜 및 시간 설정
        announcementService.createAnnouncement(announcement); // 공지사항 저장
        return "redirect:/guest/noticeList"; // 목록 페이지로 리다이렉트
    }

    // 파일 확장자 추출 메서드
    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.') + 1);
        }
        return "";
    }

    // 공지사항 수정 페이지
    @GetMapping("/admin/noticeEdit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Announcement announcement = announcementService.getAnnouncementById(id).orElse(null);
        model.addAttribute("announcement", announcement);
        return "admin/noticeEdit"; // 수정된 경로 및 파일명
    }

    // 공지사항 수정 처리
    @PostMapping("/admin/notice/update/{idx}")
    public String updateNotice(@PathVariable Long idx,
                               @RequestParam String title,
                               @RequestParam String content,
                               @RequestParam(required = false) MultipartFile inquiry_ofile,
                               @RequestParam(required = false) boolean deleteFile,
                               RedirectAttributes redirectAttributes) {

        Announcement announcement = announcementService.findById(idx);
        if (announcement != null) {
            announcement.setTitle(title);
            announcement.setContent(content);

            // Handle file deletion
            if (deleteFile) {
                // Add logic to delete the file from the server if necessary
                File existingFile = new File(System.getProperty("user.home") + "/uploads/" + announcement.getSfile());
                if (existingFile.exists()) {
                    existingFile.delete(); // Delete the file from the filesystem
                }
                announcement.setOfile(null); // Assuming you store the original file name in 'ofile'
                announcement.setSfile(null); // Assuming you store the secure file name in 'sfile'
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
                    announcement.setOfile(fileName); // Save the uploaded file name
                    announcement.setSfile(fileName); // Store the filename for future reference
                } catch (IOException e) {
                    // Handle the exception
                    redirectAttributes.addFlashAttribute("error", "파일 업로드 중 오류가 발생했습니다.");
                }
            }

            announcementService.saveAnnouncement(announcement);
            redirectAttributes.addFlashAttribute("message", "게시물이 수정되었습니다.");
        }

        return "redirect:/guest/noticeList";
    }

    // 공지사항 삭제 처리
    @GetMapping("/admin/notice/delete/{id}")
    public String deleteAnnouncement(@PathVariable("id") Long id) {
        announcementService.deleteAnnouncement(id);
        return "redirect:/guest/noticeList";
    }

    @GetMapping("/admin/files/{filename}")
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
                    .contentType(MediaType.parseMediaType(contentType)) // Set content type
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename)
                    .body(resource);

        } catch (IOException e) {
            // Log the exception (optional)
            e.printStackTrace();  // For debugging

            return ResponseEntity.notFound().build();
        }
    }
}
