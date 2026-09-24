package com.temquadra.temquadra_api.controller



import com.temquadra.temquadra_api.dto.*
import com.temquadra.temquadra_api.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<LoginResponse> {
        val response = authService.registrar(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}