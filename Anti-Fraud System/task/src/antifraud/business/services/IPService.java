package antifraud.business.services;

import antifraud.business.exception.EntityNotFoundException;
import antifraud.business.exception.IPAlreadyExistException;
import antifraud.business.model.entity.IP;
import antifraud.persistence.IPRepository;
import antifraud.presentation.DTO.ip.IPRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IPService {
    private final IPRepository ipRepository;

    @Autowired
    public IPService(IPRepository ipRepository) {
        this.ipRepository = ipRepository;
    }

    public IP saveSuspiciousIp(IPRequestDTO ipRequestDTO) {
        Optional<IP> existingIP = ipRepository.findByIp(ipRequestDTO.ip());

        if (existingIP.isPresent()) {
            throw new IPAlreadyExistException("IP %s already exist!".formatted(ipRequestDTO.ip()));
        }

        return ipRepository.save(new IP(ipRequestDTO.ip()));
    }


    public void deleteSuspiciousIP(String ipAddress) {
        Optional<IP> ipToBeDeleted = ipRepository.findByIp(ipAddress);

        if (ipToBeDeleted.isEmpty()) {
            throw new EntityNotFoundException("IP %s doesn't exist.".formatted(ipAddress));
        }

        ipRepository.delete(ipToBeDeleted.get());
    }
}
