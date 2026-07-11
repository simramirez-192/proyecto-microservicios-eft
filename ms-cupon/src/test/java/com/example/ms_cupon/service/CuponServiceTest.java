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

    @Test
    void listarCupones_listaVacia_retornaListaVacia() {
        when(cuponRepository.findAll()).thenReturn(List.of());

        List<CuponResponseDTO> resultado = cuponService.listarCupones();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarCupones_multiplesCupones_retornaTodos() {
        Cupon cupon2 = new Cupon();
        cupon2.setId(2L);
        cupon2.setCodigo("DESCUENTO20");
        cupon2.setPorcentajeDescuento(20.0);
        cupon2.setActivo(false);

        when(cuponRepository.findAll()).thenReturn(List.of(cupon, cupon2));

        List<CuponResponseDTO> resultado = cuponService.listarCupones();

        assertEquals(2, resultado.size());
        assertEquals("DESCUENTO10", resultado.get(0).getCodigo());
        assertEquals("DESCUENTO20", resultado.get(1).getCodigo());
        assertTrue(resultado.get(0).getActivo());
        assertFalse(resultado.get(1).getActivo());
    }

    @Test
    void crearCupon_camposDelDTOSonCorrectos() {
        Cupon guardado = new Cupon();
        guardado.setId(5L);
        guardado.setCodigo("BLACKFRIDAY");
        guardado.setPorcentajeDescuento(50.0);
        guardado.setActivo(true);

        CuponRequestDTO nuevoRequest = new CuponRequestDTO();
        nuevoRequest.setCodigo("BLACKFRIDAY");
        nuevoRequest.setPorcentajeDescuento(50.0);

        when(cuponRepository.findByCodigo("BLACKFRIDAY")).thenReturn(Optional.empty());
        when(cuponRepository.save(any(Cupon.class))).thenReturn(guardado);

        CuponResponseDTO resultado = cuponService.crearCupon(nuevoRequest);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals("BLACKFRIDAY", resultado.getCodigo());
        assertEquals(50.0, resultado.getPorcentajeDescuento());
        assertTrue(resultado.getActivo());

        verify(cuponRepository).save(argThat(c ->
                c.getCodigo().equals("BLACKFRIDAY") &&
                c.getPorcentajeDescuento().equals(50.0) &&
                c.getActivo().equals(true)
        ));
    }

    @Test
    void actualizarCupon_cuponExistente_actualizaCorrectamente() {
        CuponRequestDTO updateDTO = new CuponRequestDTO();
        updateDTO.setCodigo("NUEVO_CODIGO");
        updateDTO.setPorcentajeDescuento(25.0);

        Cupon actualizado = new Cupon();
        actualizado.setId(1L);
        actualizado.setCodigo("NUEVO_CODIGO");
        actualizado.setPorcentajeDescuento(25.0);
        actualizado.setActivo(true);

        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cupon));
        when(cuponRepository.save(any(Cupon.class))).thenReturn(actualizado);

        CuponResponseDTO resultado = cuponService.actualizarCupon(1L, updateDTO);

        assertNotNull(resultado);
        assertEquals("NUEVO_CODIGO", resultado.getCodigo());
        assertEquals(25.0, resultado.getPorcentajeDescuento());

        verify(cuponRepository).save(argThat(c ->
                c.getCodigo().equals("NUEVO_CODIGO") &&
                c.getPorcentajeDescuento().equals(25.0)
        ));
    }

    @Test
    void buscarPorId_cuponInactivo_retornaDTO() {
        Cupon inactivo = new Cupon();
        inactivo.setId(3L);
        inactivo.setCodigo("EXPIRADO");
        inactivo.setPorcentajeDescuento(5.0);
        inactivo.setActivo(false);

        when(cuponRepository.findById(3L)).thenReturn(Optional.of(inactivo));

        CuponResponseDTO resultado = cuponService.buscarPorId(3L);

        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("EXPIRADO", resultado.getCodigo());
        assertFalse(resultado.getActivo());
    }

    @Test
    void listarCupones_verificarConversionDTO() {
        Cupon cupon2 = new Cupon();
        cupon2.setId(2L);
        cupon2.setCodigo("VERANO30");
        cupon2.setPorcentajeDescuento(30.0);
        cupon2.setActivo(true);

        when(cuponRepository.findAll()).thenReturn(List.of(cupon, cupon2));

        List<CuponResponseDTO> resultado = cuponService.listarCupones();

        assertEquals(1L, resultado.get(0).getId());
        assertEquals("DESCUENTO10", resultado.get(0).getCodigo());
        assertEquals(10.0, resultado.get(0).getPorcentajeDescuento());
        assertTrue(resultado.get(0).getActivo());

        assertEquals(2L, resultado.get(1).getId());
        assertEquals("VERANO30", resultado.get(1).getCodigo());
        assertEquals(30.0, resultado.get(1).getPorcentajeDescuento());
    }
}
