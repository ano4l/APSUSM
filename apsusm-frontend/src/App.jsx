import React from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { TranslationProvider } from './contexts/TranslationContext'
import Navbar from './components/Navbar'
import HomePage from './pages/HomePage'
import RegisterPage from './pages/RegisterPage'
import PaymentVerifyPage from './pages/PaymentVerifyPage'
import SuccessPage from './pages/SuccessPage'
import VerifyMemberPage from './pages/VerifyMemberPage'
import AdminLoginPage from './pages/AdminLoginPage'
import AdminDashboard from './pages/AdminDashboard'

function AppContent() {
  return (
    <div className="min-h-screen bg-white text-slate-600 font-sans">
      <Navbar />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/payment/verify" element={<PaymentVerifyPage />} />
        <Route path="/success/:id" element={<SuccessPage />} />
        <Route path="/verify/:memberId" element={<VerifyMemberPage />} />
        <Route path="/admin" element={<AdminLoginPage />} />
        <Route path="/admin/dashboard" element={<AdminDashboard />} />
      </Routes>
    </div>
  )
}

export default function App() {
  return (
    <TranslationProvider>
      <BrowserRouter>
        <AppContent />
      </BrowserRouter>
    </TranslationProvider>
  )
}
