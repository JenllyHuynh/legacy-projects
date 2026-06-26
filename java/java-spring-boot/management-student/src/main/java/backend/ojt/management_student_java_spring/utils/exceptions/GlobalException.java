package backend.ojt.management_student_java_spring.utils.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import backend.ojt.management_student_java_spring.domain.dto.res.RestResponse;

@RestControllerAdvice
public class GlobalException {
    /**
     * handle resource not found
     * 
     * @param ne
     * @return http status code 404
     **/
    @ExceptionHandler(value = { ResourceNotFoundException.class })
    public ResponseEntity<RestResponse<Object>> handleNotFoundException(ResourceNotFoundException ne) {
        var statusCode = HttpStatus.NOT_FOUND.value();
        var res = new RestResponse<>();
        res.setStatusCode(statusCode);
        res.setError("Not found!");
        res.setMessage(ne.getMessage());
        return ResponseEntity.status(statusCode).body(res);
    }

    /**
     * handle conflict
     * 
     * @param ne
     * @return http status code 409
     **/
    @ExceptionHandler(value = { ConflictDataException.class })
    public ResponseEntity<RestResponse<Object>> handleConflictException(ConflictDataException ne) {
        var statusCode = HttpStatus.CONFLICT.value();
        var res = new RestResponse<>();
        res.setStatusCode(statusCode);
        res.setError("Conflict!");
        res.setMessage(ne.getMessage());
        return ResponseEntity.status(statusCode).body(res);
    }

    /**
     * bad request
     * 
     * @param ne
     * @return http status code 400
     **/
    @ExceptionHandler(value = { RequestErrorException.class })
    public ResponseEntity<RestResponse<Object>> handleBadDateException(RequestErrorException ne) {
        var statusCode = HttpStatus.BAD_REQUEST.value();
        var res = new RestResponse<>();
        res.setStatusCode(statusCode);
        res.setError("Bad request!");
        res.setMessage(ne.getMessage());
        return ResponseEntity.status(statusCode).body(res);
    }

    /**
     * Handle exception for valid request
     * 
     * @param ex
     * @return bad request 400
     **/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        var res = new RestResponse<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    errors.put(
                            error.getField(),
                            error.getDefaultMessage());
                });
        res.setError("Bad request data!");
        res.setMessage(errors);
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    /**
     * hanlde case when user not logined
     * 
     * @param ne
     * @return http status code 403
     **/
    @ExceptionHandler(value = { UnauthorizedException.class })
    public ResponseEntity<RestResponse<Object>> handleDenyException(UnauthorizedException ne) {
        var statusCode = HttpStatus.UNAUTHORIZED.value();
        var res = new RestResponse<>();
        res.setStatusCode(statusCode);
        res.setError("Not logged in!");
        res.setMessage(ne.getMessage());
        return ResponseEntity.status(statusCode).body(res);
    }

    /**
     * handle error logined but, do have not permission
     * 
     * @param ne
     * @return
     **/
    @ExceptionHandler(value = { AccessToResourceException.class })
    public ResponseEntity<RestResponse<Object>> handleAccessToResourcesException(AccessToResourceException ne) {
        var statusCode = HttpStatus.FORBIDDEN.value();
        var res = new RestResponse<>();
        res.setStatusCode(statusCode);
        res.setError("Forbidden");
        res.setMessage(ne.getMessage());
        return ResponseEntity.status(statusCode).body(res);
    }

    /*
     * invalid format
     */
    @ControllerAdvice
    public class GlobalExceptionHandler {
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
            var res = new RestResponse<>();
            var statusCode = HttpStatus.BAD_REQUEST.value();
            res.setStatusCode(statusCode);
            res.setError("Bad request");
            res.setMessage("Data invalid format!");
            return ResponseEntity.status(statusCode).body(res);

        }
    }

}
