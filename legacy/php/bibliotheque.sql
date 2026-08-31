-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : ven. 07 mars 2025 à 08:09
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `bibliotheque`
--

-- --------------------------------------------------------

--
-- Structure de la table `books`
--

CREATE TABLE `books` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `author` varchar(255) NOT NULL,
  `published_year` int(11) DEFAULT NULL,
  `genre` varchar(100) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `books`
--

INSERT INTO `books` (`id`, `title`, `author`, `published_year`, `genre`, `image`, `created_at`) VALUES
(28, 'main', 'durel', 2000, 'education', 'uploads/be75ae75bd9e2fdfe16f11e6267fb85b.png', '2025-03-05 12:44:20'),
(29, 'ok', 'jean', 2, 'education', 'uploads/2.jpg', '2025-03-05 12:46:45'),
(38, 'L\' art de la guerre', 'SUN TSU', 768, 'Strategy', 'uploads/IMG_20241125_122005_280@-289400246.jpg', '2025-03-05 16:43:21');

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `email`) VALUES
(3, 'Wany', '$2y$10$DeKp7d.UbuEtrYju5owUZ.w7i6XiiPGnN1wn96XhMSJ5lX..eCR.a', 'waniprincesse04@gmail.com'),
(6, 'BAMI', '$2y$10$Lqd0CbMqEtS7.1oUVnprou31YRPx0SACalSzI/ZKbuBHp3/0.RYEq', 'cooolup@gmail.com'),
(9, 'Andy', '$2y$10$sOQnDtXy5PohwVAN.s3yEeL4ly6itWNwrjrcOxMpjCbvQfniW0lre', 'andybig@gmail.com'),
(10, 'Romeo', '$2y$10$x54YVDTTN8xZspK1L6HDfuu8dakhDildoytu65ByDJbhbLO7TLxDG', 'romeoup@gmail.com'),
(12, 'sany', '$2y$10$rVNgkFhFRY9tXmDBKYkrR.1JG1e/lLq7tUKqjnI1Lg50NVgvyKG/6', 'sanyup@gmail.com'),
(47, 'durel', '$2y$10$/yR3sM193awPsqTjYA3OnOK8GKWFgMGNn/kuHR2ienGahEU3z3VsS', 'durel123@gmail.com'),
(48, 'The Prince IBRAHIM', '$2y$10$34TzVRTFFPi8VDVW87S/nOQf.dAUyosvjJYRrccG3NCV3gLDQBtmq', 'me@theprinceibrahim.com'),
(51, 'durel5', '$2y$10$urBcwEFGvrToe3aoNGq24OfjZ01wcLAyWL/XErwcVaShMO4Fv.6zm', 'd@gmail.com'),
(52, 'durel4', '$2y$10$qaNYi5dmxvNC50ShlJNb0u3QmrySURMpDHL.dxlc8MX98TGUWg/JO', 'd4@gmail.com');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `books`
--
ALTER TABLE `books`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `books`
--
ALTER TABLE `books`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
