# 📋 Instrucciones de Configuración - Variables de Entorno

## Cambios Realizados

El archivo `src/main/resources/application.properties` ahora está **completamente limpio de secretos**. Todos los valores sensibles se definan como **variables de entorno**.

## 📌 Variables Requeridas

El proyecto necesita las siguientes variables de entorno para funcionar:

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `API_KEY` | Clave secreta para firmar JWT | `openssl rand -base64 32` |
| `URL` | URL del frontend (CORS) | `http://localhost:3000` |
| `DB_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://host:25339/db?sslmode=require` |
| `DB_USER` | Usuario de base de datos | `avnadmin` |
| `DB_PASS` | Contraseña de base de datos | `contraseña_aqui` |
| `MAIL_FROM` | Email remitente SMTP | `tu@gmail.com` |
| `CLOUDINARY_CLOUD_NAME` | Nombre de nube Cloudinary | `dtipujfyp` |
| `CLOUDINARY_API_KEY` | API Key de Cloudinary | `498979736334734` |
| `CLOUDINARY_API_SECRET` | API Secret de Cloudinary | `mZ3rwANzer...` |

## 🚀 Cómo Usar

### Opción 1: En Desarrollo Local con IDE

1. **Copiar `.env.local` a `.env`**
   ```bash
   copy .env.local .env
   ```
   (Solo local, no subir a Git)

2. **Instalar el plugin de .env en tu IDE** (si lo tienes):
   - VS Code: extensión "DotENV"
   - IntelliJ: soporte nativo

3. **O definir variables en tu terminal** (PowerShell):
   ```powershell
   $env:API_KEY = "tu_clave_aqui"
   $env:URL = "http://localhost:3000"
   $env:DB_URL = "jdbc:postgresql://..."
   $env:DB_USER = "avnadmin"
   $env:DB_PASS = "AVNS_..."
   $env:MAIL_FROM = "tu@gmail.com"
   $env:CLOUDINARY_CLOUD_NAME = "dtipujfyp"
   $env:CLOUDINARY_API_KEY = "498979736334734"
   $env:CLOUDINARY_API_SECRET = "mZ3rwANzer..."
   ```

4. **Luego ejecutar Maven**:
   ```bash
   mvn spring-boot:run
   ```

### Opción 2: En Docker (Producción)

Pasar variables al contenedor:
```bash
docker run -e API_KEY="value" \
           -e URL="value" \
           -e DB_URL="value" \
           ... CinemaBack:latest
```

### Opción 3: En Docker Compose

Crear archivo `.env` en la raíz y usarlo en docker-compose.yml:
```yaml
version: '3.8'
services:
  cinema-back:
    environment:
      - API_KEY=${API_KEY}
      - URL=${URL}
      - DB_URL=${DB_URL}
      ...
```

### Opción 4: En CI/CD (GitHub Actions, Jenkins, etc.)

Definir **secrets** en la plataforma:
- Configurar secretos en GitHub → Settings → Secrets
- Pasarlos al build con: `mvn -DAPI_KEY=${{ secrets.API_KEY }} ...`

## ⚠️ Seguridad Importante

1. **`.env` nunca se sube a Git**
   - Verifica que `.gitignore` contiene `.env`
   - En `.env.local` ya está para desarrollo

2. **`.env.example` sí se sube a Git**
   - Sirve como plantilla (sin secretos)
   - Otros desarrolladores lo copian y llenan

3. **Genera un `API_KEY` único y seguro**:
   ```bash
   # En PowerShell:
   [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((New-Guid).ToString())) + (New-Guid).ToString().Replace("-","")
   ```

## ✅ Compilar y Probar

Después de definir las variables:

```bash
# Compilar sin tests
mvn compile

# O compilar con tests
mvn test

# O ejecutar directamente
mvn spring-boot:run
```

## 🔍 Verificar Variables

Si algo falla, comprueba que todas las variables están definidas:
```powershell
$env:API_KEY
$env:URL
$env:DB_URL
# ... etc
```

Si no aparecen, vuelve a definirlas o abre una nueva terminal.

## 📞 Próximos Pasos

Después de esto:
1. Define todas las variables de entorno
2. Verifica que la BD de Aiven está accesible
3. Verifica que Cloudinary y SMTP funcionan
4. Compila el proyecto
5. Ejecuta: `mvn spring-boot:run`
