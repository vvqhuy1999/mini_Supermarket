package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.SanPhamRepository;
import com.example.mini_supermarket.repository.GiaSanPhamRepository;
import com.example.mini_supermarket.entity.GiaSanPham;
import com.example.mini_supermarket.entity.SanPham;
import com.example.mini_supermarket.dto.SanPhamOptimizedDto;
import com.example.mini_supermarket.service.SanPhamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SanPhamServiceImpl implements SanPhamService {
    private SanPhamRepository sanPhamRepository;
    private GiaSanPhamRepository giaSanPhamRepository;

    public SanPhamServiceImpl(SanPhamRepository sanPhamRepository, GiaSanPhamRepository giaSanPhamRepository) {
        this.sanPhamRepository = sanPhamRepository;
        this.giaSanPhamRepository = giaSanPhamRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPham> findAll() {
        return sanPhamRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPham> findAllActive() {
        return sanPhamRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public SanPham findById(String theId) {
        Optional<SanPham> result = sanPhamRepository.findById(theId);
        SanPham theSanPham = null;

        if (result.isPresent()) {
            theSanPham = result.get();
        } else {
            throw new RuntimeException("Did not find SanPham id - " + theId);
        }
        return theSanPham;
    }

    @Override
    @Transactional(readOnly = true)
    public SanPham findActiveById(String theId) {
        Optional<SanPham> result = sanPhamRepository.findActiveById(theId);
        SanPham theSanPham = null;

        if (result.isPresent()) {
            theSanPham = result.get();
            // set gia hien tai
            theSanPham.setGiaHienTai(getCurrentPrice(theId));
        } else {
            throw new RuntimeException("Did not find active SanPham id - " + theId);
        }
        return theSanPham;
    }

    @Override
    @Transactional
    public SanPham save(SanPham theSanPham) {
        return sanPhamRepository.save(theSanPham);
    }

    @Override
    @Transactional
    public void deleteById(String theId) {
        sanPhamRepository.deleteById(theId);
    }

    @Override
    @Transactional
    public void softDeleteById(String theId) {
        Optional<SanPham> result = sanPhamRepository.findByIdIncludeDeleted(theId);
        
        if (result.isPresent()) {
            SanPham sanPham = result.get();
            sanPham.setIsDeleted(true);
            sanPhamRepository.save(sanPham);
        } else {
            throw new RuntimeException("Did not find SanPham id - " + theId);
        }
    }

    @Override
    @Transactional
    public SanPham update(SanPham sanPham) {
        Optional<SanPham> existingSanPham = sanPhamRepository.findById(sanPham.getMaSP());

        if (!existingSanPham.isPresent()) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID - " + sanPham.getMaSP());
        }

        return sanPhamRepository.save(sanPham);
    }

    @Override
    @Transactional(readOnly = true)
    public java.math.BigDecimal getCurrentPrice(String maSP) {
        // 1) Ưu tiên khoảng giá đang hiệu lực theo ngày
        List<GiaSanPham> applicable = giaSanPhamRepository.findApplicablePrices(maSP);
        if (!applicable.isEmpty()) {
            return applicable.get(0).getGia();
        }
        // 2) Fallback: lấy bản ghi giá mới nhất
        List<GiaSanPham> latest = giaSanPhamRepository.findLatestPrices(maSP);
        if (!latest.isEmpty()) {
            return latest.get(0).getGia();
        }
        return null;
    }
    
    // === IMPLEMENTATION CHO METHODS TỐI ƯU - SỬ DỤNG DTO ===
    
    @Override
    @Transactional(readOnly = true)
    public List<SanPhamOptimizedDto> findAllActiveOptimized() {
        List<SanPhamOptimizedDto> list = sanPhamRepository.findAllActiveOptimized();
        if (list != null) {
            for (SanPhamOptimizedDto dto : list) {
                dto.setGiaHienTai(getCurrentPrice(dto.getMaSP()));
            }
        }
        return list;
    }
    
    @Override
    @Transactional(readOnly = true)
    public SanPhamOptimizedDto findActiveByIdOptimized(String id) {
        Optional<SanPhamOptimizedDto> result = sanPhamRepository.findActiveByIdOptimized(id);
        SanPhamOptimizedDto theSanPham = null;

        if (result.isPresent()) {
            theSanPham = result.get();
            theSanPham.setGiaHienTai(getCurrentPrice(theSanPham.getMaSP()));
        } else {
            throw new RuntimeException("Did not find active SanPham id - " + id);
        }
        return theSanPham;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SanPhamOptimizedDto> findByCategoryOptimized(String maLoaiSP) {
        List<SanPhamOptimizedDto> list = sanPhamRepository.findByCategoryOptimized(maLoaiSP);
        if (list != null) {
            for (SanPhamOptimizedDto dto : list) {
                dto.setGiaHienTai(getCurrentPrice(dto.getMaSP()));
            }
        }
        return list;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SanPhamOptimizedDto> findByCategoryAndActiveOptimized(String maLoaiSP) {
        List<SanPhamOptimizedDto> list = sanPhamRepository.findByCategoryAndActiveOptimized(maLoaiSP);
        if (list != null) {
            for (SanPhamOptimizedDto dto : list) {
                dto.setGiaHienTai(getCurrentPrice(dto.getMaSP()));
            }
        }
        return list;
    }
    
    // === IMPLEMENTATION CHO METHODS MỚI - VỚI SỐ LƯỢNG TỒN KHO ===
    
    @Override
    @Transactional(readOnly = true)
    public List<SanPhamOptimizedDto> findAllActiveWithTonKho() {
        List<SanPhamOptimizedDto> list = sanPhamRepository.findAllActiveWithTonKho();
        if (list != null) {
            for (SanPhamOptimizedDto dto : list) {
                dto.setGiaHienTai(getCurrentPrice(dto.getMaSP()));
            }
        }
        return list;
    }
    
    @Override
    @Transactional(readOnly = true)
    public SanPhamOptimizedDto findActiveByIdWithTonKho(String id) {
        Optional<SanPhamOptimizedDto> result = sanPhamRepository.findActiveByIdWithTonKho(id);
        SanPhamOptimizedDto theSanPham = null;

        if (result.isPresent()) {
            theSanPham = result.get();
            theSanPham.setGiaHienTai(getCurrentPrice(theSanPham.getMaSP()));
        } else {
            throw new RuntimeException("Did not find active SanPham id - " + id);
        }
        return theSanPham;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPhamOptimizedDto> findAllActiveWithTonKhoByKho(String maKho) {
        List<SanPhamOptimizedDto> list = sanPhamRepository.findAllActiveWithTonKhoByKho(maKho);
        if (list != null) {
            for (SanPhamOptimizedDto dto : list) {
                dto.setGiaHienTai(getCurrentPrice(dto.getMaSP()));
            }
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public SanPhamOptimizedDto findActiveByIdWithTonKhoByKho(String id, String maKho) {
        Optional<SanPhamOptimizedDto> result = sanPhamRepository.findActiveByIdWithTonKhoByKho(id, maKho);
        SanPhamOptimizedDto theSanPham = null;

        if (result.isPresent()) {
            theSanPham = result.get();
            theSanPham.setGiaHienTai(getCurrentPrice(theSanPham.getMaSP()));
        } else {
            throw new RuntimeException("Did not find active SanPham id - " + id);
        }
        return theSanPham;
    }
} 
