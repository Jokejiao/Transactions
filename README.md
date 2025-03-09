# Transactions App

## Overview
This is a modern Android application following the best practices of Android development. It manages and displays transaction data with an **offline-first** approach using a local Room database as the **Single Source of Truth (SSOT)**.

## Features 
- Modern Android App Architecture
- **Dependency Injection** with **Hilt**
- **Layered Transaction Data Models** for Local, Remote, and UI display
- **Offline-First Repository** with Room Database as **SSOT**
- Initial Transaction Fetch from Network on First Launch
- Manual Data Refresh via "Refresh" Button
- GST Calculation Implementation
- Debit/Credit Transaction Colors
- TalkBack Accessibility Support
- Unit and Instrumentation Test Coverage

## Tech Stack
- Kotlin – Primary language for development
- MVVM Architecture – Modern app architecture
- Hilt – Dependency Injection
- Room – Local database for offline persistence
- Retrofit – API calls to fetch transaction data
- Coroutines & Flow – Asynchronous programming
- Jetpack Compose – UI development
- Material Design 3 – Modern UI components
- Accessibility (TalkBack) – Improved user experience for visually impaired users
- JUnit & Compose UI Test – Unit and Instrumentation testing

## How It Works
On the first launch, the app fetches transaction data from the network and stores it in the Room database.
The app displays the transactions, where debit and credit transactions are shown with different text colors.
Users can refresh the data anytime by clicking the "Refresh" button in the top app bar.
The Room database acts as the Single Source of Truth (SSOT), ensuring offline access to transactions.
TalkBack is enabled for accessibility, providing voice feedback when interacting with transactions.