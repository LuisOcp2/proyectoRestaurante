package com.mosqueteros.proyecto_restaurante.util;

import com.mosqueteros.proyecto_restaurante.dao.ConfiguracionDAO;

public final class ConfiguracionUtil {

    public static final String CLAVE_COMPANY_NAME = "company_name";
    public static final String CLAVE_COMPANY_ADDRESS = "company_address";
    public static final String CLAVE_COMPANY_PHONE = "company_phone";
    public static final String CLAVE_COMPANY_EMAIL = "company_email";
    public static final String CLAVE_CURRENCY_SYMBOL = "currency_symbol";
    public static final String CLAVE_CURRENCY_CODE = "currency_code";
    public static final String CLAVE_CURRENCY_DECIMALS = "currency_decimals";
    public static final String CLAVE_TAX_PERCENTAGE = "tax_percentage";
    public static final String CLAVE_DEFAULT_TIP_PERCENTAGE = "default_tip_percentage";
    public static final String CLAVE_TICKET_FOOTER = "ticket_footer";
    public static final String CLAVE_INVOICE_PREFIX = "invoice_prefix";
    public static final String CLAVE_TIMEZONE = "timezone";
    public static final String CLAVE_UI_THEME = "ui_theme";
    public static final String CLAVE_UI_QUICK_PROFILE = "ui_quick_profile";
    public static final String CLAVE_SEDE_ACTIVA_USUARIO_PREFIJO = "ui_active_sede_user_";

    public static final String TEMA_UI_PREDETERMINADO = "violet";

    private ConfiguracionUtil() {
        throw new UnsupportedOperationException("Clase utilitaria");
    }

    public static String obtener(String clave, String valorPorDefecto) {
        try {
            String valor = ConfiguracionDAO.obtenerValorPorClave(clave);
            if (valor == null || valor.isBlank()) {
                return valorPorDefecto;
            }
            return valor;
        } catch (Exception e) {
            return valorPorDefecto;
        }
    }

    public static int obtenerEntero(String clave, int valorPorDefecto) {
        String valor = obtener(clave, String.valueOf(valorPorDefecto));
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return valorPorDefecto;
        }
    }

    public static double obtenerDecimal(String clave, double valorPorDefecto) {
        String valor = obtener(clave, String.valueOf(valorPorDefecto));
        try {
            return Double.parseDouble(valor.trim());
        } catch (NumberFormatException e) {
            return valorPorDefecto;
        }
    }

    public static String nombreEmpresa() {
        return obtener(CLAVE_COMPANY_NAME, "Mi Restaurante");
    }

    public static String simboloMoneda() {
        return obtener(CLAVE_CURRENCY_SYMBOL, "$");
    }

    public static String pieTicket() {
        return obtener(CLAVE_TICKET_FOOTER, "Gracias por su compra.");
    }

    public static String temaUI() {
        return obtener(CLAVE_UI_THEME, TEMA_UI_PREDETERMINADO);
    }

    public static String claveSedeActivaPorUsuario(long usuId) {
        return CLAVE_SEDE_ACTIVA_USUARIO_PREFIJO + usuId;
    }
}
