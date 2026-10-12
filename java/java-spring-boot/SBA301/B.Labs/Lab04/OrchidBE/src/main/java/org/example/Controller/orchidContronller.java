package org.example.Controller;

import org.example.Entities.orchid;
import org.example.Services.orchidServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class orchidContronller {
    @Autowired
    orchidServices services;

    @RequestMapping("/orchids")
    @ResponseBody
    public List<orchid> showOrchid(Model model) {
        List<orchid> list = services.findAll();

        // Sắp xếp theo tên (alphabet)
        list.sort((s1, s2) -> {
            String name1 = s1.getName();
            String name2 = s2.getName();
            if (name1 == null && name2 == null) return 0;
            if (name1 == null) return 1;
            if (name2 == null) return -1;
            return name1.compareToIgnoreCase(name2);
        });
//
//        // Nếu muốn sắp xếp theo tuổi thì thay bằng:
//        // list.sort(Comparator.comparingInt(Staff::getAge));
//
//        // Nếu muốn sắp xếp theo nhiều tiêu chí (tên rồi đến tuổi):
//        // list.sort(Comparator.comparing(Staff::getStaffName)
//        //                     .thenComparingInt(Staff::getAge));

        model.addAttribute("List", list);
        //return "ListTour";
        return list;
    }
    @DeleteMapping("orchids/{id}")
    public void deleteOrchid(@PathVariable("id") int id) {
        services.deleteOrchid(id);
    }

    @PostMapping("orchids")
    public orchid addOrchid(@RequestBody orchid orchid) {
        return services.saveOrchid(orchid);
    }

    @PutMapping("orchids/{id}")
    public orchid updateOrchid(@PathVariable("id") int id, @RequestBody orchid orchidDetails) {
        return services.updateOrchid(id, orchidDetails);
    }

    @GetMapping("orchids/{id}")
    public orchid getOrchidById(@PathVariable("id") int id) {
        return services.findById(id);
    }

    @GetMapping("orchids/search")
    public List<orchid> searchOrchids(@RequestParam("name") String name) {
        return services.searchByName(name);
    }

}
