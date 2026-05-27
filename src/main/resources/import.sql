insert into movies (active, duration, price, genre, poster_path, title) values (true, '01:40:00', 12.5, 'Accion', 'https://res.cloudinary.com/dtipujfyp/image/upload/v1778432856/Logo_pmbwud.jpg', 'Pelicula de prueba');

insert into rooms (status, name, tipo) values (true, 'Sala 1', '2D');

insert into showtimes (active, id_movie, id_room, showtime, language) values (true, 1, 1, '2026-05-20 18:30:00', 'Español');

insert into users (email, fullname, password, rol) values ('admin.cineplus@example.com', 'Administrador Prueba', '$2a$10$oxR8ct9//Wur811bUEZXleyjqrnQtUew/d0r7RyeYzqoqTyu4tJMm', 'ADMIN');