package antifraud.presentation.controller;

import antifraud.business.model.entity.IP;
import antifraud.business.model.enums.TransactionStatus;
import antifraud.business.services.IPService;
import antifraud.business.services.TransactionService;
import antifraud.presentation.DTO.StatusResponseDTO;
import antifraud.presentation.DTO.ip.IPRequestDTO;
import antifraud.presentation.DTO.ip.IPResponseDTO;
import antifraud.presentation.DTO.transaction.TransactionDTO;
import antifraud.presentation.validation.ValidIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@Validated
public class AntiFraudController {
    private final TransactionService transactionService;
    private final IPService ipService;

    @Autowired
    public AntiFraudController(TransactionService transactionService, IPService ipService) {
        this.transactionService = transactionService;
        this.ipService = ipService;
    }

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<Map<String, TransactionStatus>> checkFraud(@RequestBody @Valid TransactionDTO transaction) {
        TransactionStatus status = transactionService.checkFraud(transaction);
        return new ResponseEntity<>(Map.of("result", status), HttpStatus.OK);
    }

    @PostMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<IPResponseDTO> saveSuspiciousIP(@RequestBody @Valid IPRequestDTO ipRequestDTO) {
        IP savedIP = ipService.saveSuspiciousIp(ipRequestDTO);
        return new ResponseEntity<>(new IPResponseDTO(savedIP.getId(), savedIP.getIp()), HttpStatus.OK);
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    @Validated
    public ResponseEntity<StatusResponseDTO> removeSuspiciousIP(@PathVariable("ip") @ValidIP String ipAddress) {
        ipService.deleteSuspiciousIP(ipAddress);

        return new ResponseEntity<>(new StatusResponseDTO("IP %s successfully removed".formatted(ipAddress)), HttpStatus.OK);
    }

}
