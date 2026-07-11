package com.example.ms_cupon.service;

import com.example.ms_cupon.dto.CuponRequestDTO;
import com.example.ms_cupon.dto.CuponResponseDTO;
import com.example.ms_cupon.model.Cupon;
import com.example.ms_cupon.repository.CuponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuponServiceTest {

    @Mock
    private CuponRepository cuponRepository;

    @InjectMocks
    private CuponService cuponService;

    private Cupon cupon;
    private CuponRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        cupon = new Cupon();
        cupon.setId(1L);
        cupon.setCodigo("DESCUENTO10");
        cupon.setPorcentajeDescuento(10.0);
        cupon.setActivo(true);

        requestDTO = new CuponRequestDTO();
        requestDTO.setCodigo("DESCUENTO10");
        requestDTO.setPorcentajeDescuento(10.0);
    }

    @Test
    void listarCupones_retornaListaDeCupones() {
        when(cuponRepository.findAll()).thenReturn(List.of(cupon));

        List<CuponResponseDTO> resultado = cuponService.listarCupones();

        assertEquals(1, resultado.size());
        assertEquals("DESCUENTO10", resultado.get(0).getCodigo());
    }

    @Test
    void buscarPorId_cuponExistente_retornaDTO() {
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cupon));

        CuponResponseDTO resultado = cuponService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(10.0, resultado.getPorcentajeDescuento());
    }

    @Test
    void buscarPorId_cuponNoExistente_lanzaExcepcion() {
        when(cuponRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> cuponService.buscarPorId(99L));
    }

    @Test
    void crearCupon_codigoNoRepite_creaCorrectamente() {
        when(cuponRepository.findByCodigo("DESCUENTO10")).thenReturn(Optional.empty());
        when(cuponRepository.save(any(Cupon.class))).thenReturn(cupon);

        CuponResponseDTO resultado = cuponService.crearCupon(requestDTO);

        assertNotNull(resultado);
        assertEquals("DESCUENTO10", resultado.getCodigo());
        verify(cuponRepository).save(any(Cupon.class));
    }

    @Test
    void crearCupon_codigoYaExiste_lanzaExcepcion() {
        when(cuponRepository.findByCodigo("DESCUENTO10")).thenReturn(Optional.of(cupon));

        assertThrows(RuntimeException.class,
                () -> cuponService.crearCupon(requestDTO));

        verify(cuponRepository, never()).save(any());
    }

    @Test
    void actualizarCupon_cuponNoExistente_lanzaExcepcion() {
        when(cuponRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> cuponService.actualizarCupon(99L, requestDTO));

        verify(cuponRepository, never()).save(any());
    }

    @Test
    void eliminarCupon_cuponExistente_eliminaCorrectamente() {
        when(cuponRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cuponRepository).deleteById(1L);

        cuponService.eliminarCupon(1L);

        verify(cuponRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarCupon_cuponNoExistente_lanzaExcepcion() {
        when(cuponRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> cuponService.eliminarCupon(99L));
    }
}
