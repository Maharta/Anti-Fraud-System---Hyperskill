package antifraud.presentation.controller;

import antifraud.business.model.entity.IP;
import antifraud.business.model.entity.StolenCard;
import antifraud.business.model.enums.TransactionStatus;
import antifraud.business.services.CardService;
import antifraud.business.services.IPService;
import antifraud.business.services.TransactionService;
import antifraud.presentation.DTO.StatusResponseDTO;
import antifraud.presentation.DTO.card.StolenCardRequestDTO;
import antifraud.presentation.DTO.card.StolenCardResponseDTO;
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
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class AntiFraudController {
    private final TransactionService transactionService;
    private final IPService ipService;
    private final CardService cardService;

    @Autowired
    public AntiFraudController(TransactionService transactionService, IPService ipService, CardService cardService) {
        this.transactionService = transactionService;
        this.ipService = ipService;
        this.cardService = cardService;
    }

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<Map<String, TransactionStatus>> checkFraud(@RequestBody @Valid TransactionDTO transaction) {
        TransactionStatus status = transactionService.checkFraud(transaction);
        return new ResponseEntity<>(Map.of("result", status), HttpStatus.OK);
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<List<IPResponseDTO>> getAllSuspiciousIP() {
        List<IPResponseDTO> ipResponseDTOS = ipService.getAllSuspiciousIPDTO();
        return new ResponseEntity<>(ipResponseDTOS, HttpStatus.OK);
    }

    @PostMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<IPResponseDTO> saveSuspiciousIP(@RequestBody @Valid IPRequestDTO ipRequestDTO) {
        IP savedIP = ipService.saveSuspiciousIp(ipRequestDTO);
        return new ResponseEntity<>(new IPResponseDTO(savedIP.getId(), savedIP.getIp()), HttpStatus.OK);
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public ResponseEntity<StatusResponseDTO> removeSuspiciousIP(@PathVariable("ip") @ValidIP String ipAddress) {
        ipService.deleteSuspiciousIP(ipAddress);

        return new ResponseEntity<>(new StatusResponseDTO("IP %s successfully removed".formatted(ipAddress)), HttpStatus.OK);
    }

    @PostMapping("/api/antifraud/stolencard")
    public ResponseEntity<StolenCardResponseDTO> saveStolenCard(@RequestBody @Valid StolenCardRequestDTO stolenCardRequestDTO) {
        StolenCard savedCard = cardService.saveCardAsStolen(stolenCardRequestDTO);

        return new ResponseEntity<>(new StolenCardResponseDTO(savedCard.getId(), savedCard.getNumber()), HttpStatus.OK);
    }

}
