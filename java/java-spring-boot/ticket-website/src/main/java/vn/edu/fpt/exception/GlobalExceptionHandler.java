package vn.edu.fpt.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.dto.ValidationResult;

@ControllerAdvice
public class GlobalExceptionHandler {
    @GetMapping("/404")
    public String error404() {
        return "error/404";
    }
    @ExceptionHandler(EventNotFoundException.class)
    public String handleEventNotFound() {
        return "error/404";
    }
    @ExceptionHandler({
            MaxUploadSizeExceededException.class,
            MultipartException.class,
            IllegalStateException.class
    })
    public String handleMaxSize(MaxUploadSizeExceededException ex,
                                RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("error",
                "Ảnh vượt quá dung lượng cho phép (tối đa 20MB)");
        ValidationResult result = new ValidationResult();
        result.setErrorTitle("File too large!!!");
        result.setMessage("Ảnh vượt quá dung lượng cho phép (tối đa 20MB)!");
        redirectAttributes.addFlashAttribute("hasMessage", true);
        redirectAttributes.addFlashAttribute("errorTitle", result.getErrorTitle());
        redirectAttributes.addFlashAttribute("message", result.getMessage());
        return "redirect:/staff/event/create";
    }

        @ExceptionHandler(RuntimeException.class)
        public String handleError() {
            return "redirect:/error/404";
        }

}
