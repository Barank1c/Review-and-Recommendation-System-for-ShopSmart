package hbg.rrssbackend.service;

import hbg.rrssbackend.model.SupportTicket;
import hbg.rrssbackend.repository.SupportTicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SupportTicketService {
    private final SupportTicketRepository supportTicketRepository;

    public SupportTicketService(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    public void save(SupportTicket supportTicket) {
        supportTicketRepository.save(supportTicket);
    }

    public Optional<SupportTicket> findById(long id) {return supportTicketRepository.findById(id);}

    public void removeById(long ticketId) {
        supportTicketRepository.deleteById(ticketId);
    }

    @Transactional
    public List<SupportTicket> getUserTicketsByPage(int page, int pageSize, long userId) {
        int offset = (page - 1) * pageSize;
        return supportTicketRepository.findUserTicketsByPage(offset, pageSize, userId);
    }

    @Transactional
    public List<SupportTicket> getAllTicketsByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return supportTicketRepository.findAllTicketsByPage(offset, pageSize);
    }

    public int getUserTicketCount(long userId) {
        return supportTicketRepository.getUserTicketCount(userId);
    }

    public int getAllTicketCount() {
        return supportTicketRepository.getALLTicketCount();
    }



}
