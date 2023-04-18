package antifraud.presentation.controller;

import antifraud.business.Transaction;
import antifraud.business.services.TransactionService;
import antifraud.business.enums.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/api/antifraud/transaction")
    public Map<String, TransactionStatus> checkFraud(@RequestBody Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Body not provided!");
        }

        TransactionStatus status = transactionService.checkFraud(transaction);
        return Map.of("result", status);
    }



    @GetMapping("/test")
    public String test() {
        return "/test is accessed";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException exception) {
        Map<String, String> errorMap = Map.of("error", exception.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

}
