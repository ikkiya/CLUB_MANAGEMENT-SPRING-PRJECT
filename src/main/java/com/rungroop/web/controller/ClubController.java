package com.rungroop.web.controller;

import com.rungroop.web.dto.ClubDto;
import com.rungroop.web.models.Club;
import com.rungroop.web.models.UserEntity;
import com.rungroop.web.security.SecurityUtil;
import com.rungroop.web.service.ClubService;
import com.rungroop.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class ClubController {
    private ClubService clubService;
    private UserService userService;

    @Autowired
    public ClubController(ClubService clubService, UserService userService) {
        this.userService = userService;
        this.clubService = clubService;
    }

    @GetMapping("")
    public String start() {
        return "redirect:home";
    }
    @GetMapping("/home")
    public String home() {

        return "home";
    }

    @GetMapping("/clubs")
    public String listClubs(Model model, Authentication authentication) {
        UserEntity user = new UserEntity();
        List<ClubDto> clubs = clubService.findAllClubs();
        //MN HNA
        String currentUsername = authentication.getName(); // Assuming username is used
        if(currentUsername != null) {
            user = userService.findByEmail(currentUsername);
        }
        model.addAttribute("user", user);
        //
        //EFHWEHRK
//        String username = SecurityUtil.getSessionUser();
//        if(username != null) {
//            user = userService.findByUsername(username);
//            model.addAttribute("user", user);
//        }
        //

        model.addAttribute("clubs", clubs);
        return "clubs-list";
    }

    @GetMapping("/clubs/{clubId}")
    public String clubDetail(@PathVariable("clubId") long clubId, Model model,Authentication authentication) {
        UserEntity user = new UserEntity();
        ClubDto clubDto = clubService.findClubById(clubId);
        String currentUsername = authentication.getName(); // Assuming username is used
        if(currentUsername != null) {
            user = userService.findByEmail(currentUsername);
        }
        model.addAttribute("user", user);
       // String username = SecurityUtil.getSessionUser();
       // if(username != null) {
        //    user = userService.findByUsername(username);
         //   model.addAttribute("user", user);
      //  }
       // model.addAttribute("user", user);
        model.addAttribute("club", clubDto);
        return "clubs-detail";
    }

    @GetMapping("/clubs/new")
    public String createClubForm(Model model) {
        Club club = new Club();
        model.addAttribute("club", club);
        return "clubs-create";
    }

    @GetMapping("/clubs/{clubId}/delete")
    public String deleteClub(@PathVariable("clubId")Long clubId) {
        clubService.delete(clubId);
        return "redirect:/clubs";
    }

    @GetMapping("/clubs/search")
    public String searchClub(@RequestParam(value = "query") String query, Model model) {
        List<ClubDto> clubs = clubService.searchClubs(query);
        model.addAttribute("clubs", clubs);
        return "clubs-list";
    }

    @PostMapping("/clubs/new")
    public String saveClub(@Valid @ModelAttribute("club") ClubDto clubDto, BindingResult result, Model model, Authentication authentication) {
        if(result.hasErrors()) {
            model.addAttribute("club", clubDto);
            return "clubs-create";
        }

            String currentUsername = authentication.getName(); // Assuming username is used

            // Set the created_by field


        UserEntity user = new UserEntity();
        if(currentUsername != null) {
            user = userService.findByEmail(currentUsername);
        }
        clubDto.setCreatedBy(user);
        clubService.saveClub(clubDto);

        return "redirect:/clubs";
    }

    @GetMapping("/clubs/{clubId}/edit")
    public String editClubForm(@PathVariable("clubId") Long clubId, Model model) {
        ClubDto club = clubService.findClubById(clubId);
        model.addAttribute("club", club);
        return "clubs-edit";
    }
    @PostMapping("/clubs/{clubId}/edit")
    public String updateClub(@PathVariable("clubId") Long clubId,
                             @Valid @ModelAttribute("club") ClubDto club,
                             BindingResult result, Model model) {
        if(result.hasErrors()) {
            model.addAttribute("club", club);
            return "clubs-edit";
        }
        club.setId(clubId);
        clubService.updateClub(club);
        return "redirect:/clubs";
    }
}
