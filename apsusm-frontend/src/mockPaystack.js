// Mock Paystack for development when backend isn't available
export function mockPaystackPayment(memberId, email, amount = 500000, currency = 'MZN') {
  // Simulate Paystack redirect URL
  const mockReference = `MOCK_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
  const mockAuthUrl = `http://localhost:5173/payment/verify?reference=${mockReference}`
  
  // Store mock payment data in sessionStorage for verification
  sessionStorage.setItem('mockPayment', JSON.stringify({
    reference: mockReference,
    memberId,
    email,
    amount,
    currency,
    status: 'success',
    paidAt: new Date().toISOString(),
    channel: 'mock'
  }))
  
  // Simulate redirect after 1 second
  setTimeout(() => {
    window.location.href = mockAuthUrl
  }, 1000)
  
  return {
    success: true,
    authorization_url: mockAuthUrl,
    reference: mockReference,
    access_code: 'mock_access_code'
  }
}

// Mock payment verification
export function mockVerifyPayment(reference) {
  const stored = sessionStorage.getItem('mockPayment')
  if (!stored || reference.startsWith('ps_')) {
    // Not a mock reference, let it fall through to real API
    return null
  }
  
  const payment = JSON.parse(stored)
  if (payment.reference !== reference) {
    return { success: false, message: 'Invalid reference' }
  }
  
  // Clear stored payment after verification
  sessionStorage.removeItem('mockPayment')
  
  return {
    success: true,
    message: 'Payment verified successfully',
    reference: payment.reference,
    amount: payment.amount,
    currency: payment.currency,
    paidAt: payment.paidAt,
    channel: payment.channel,
    member: {
      id: payment.memberId,
      memberId: `APSUSM-${new Date().getFullYear()}-${String(Math.floor(Math.random() * 9999) + 1).padStart(4, '0')}`,
      status: 'ACTIVE',
      hasCard: true,
      emailSent: true,
      paidAt: payment.paidAt,
      cardGeneratedAt: new Date().toISOString(),
      expiresAt: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString() // 1 year
    }
  }
}

// Mock member status
export function mockGetMemberStatus(memberId) {
  return {
    success: true,
    member: {
      id: memberId,
      memberId: `APSUSM-${new Date().getFullYear()}-${String(Math.floor(Math.random() * 9999) + 1).padStart(4, '0')}`,
      fullName: 'Dr. Ana Machava',
      email: 'ana.machava@example.com',
      licenseNumber: 'MED-2023-1234',
      specialization: 'Internal Medicine',
      institution: 'Central Hospital Maputo',
      province: 'Maputo',
      status: 'ACTIVE',
      hasCard: true,
      emailSent: true,
      registeredAt: new Date().toISOString(),
      paidAt: new Date().toISOString(),
      cardGeneratedAt: new Date().toISOString(),
      expiresAt: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString()
    }
  }
}

// Mock member verification
export function mockVerifyMember(memberId) {
  return {
    verified: true,
    memberId,
    name: 'Dr. Ana Machava',
    specialization: 'Internal Medicine',
    province: 'Maputo',
    status: 'ACTIVE',
    expiresAt: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString()
  }
}

// Mock member registration
export function mockRegisterMember(formData) {
  // Simulate backend processing delay
  return new Promise((resolve) => {
    setTimeout(() => {
      const mockId = `temp_${Date.now()}`
      resolve({
        success: true,
        message: 'Registration successful',
        id: mockId,
        status: 'PENDING_PAYMENT'
      })
    }, 500)
  })
}

// Check if we should use mock (when backend is not responding)
export function shouldUseMock() {
  // Check if we're in development and backend might not be available
  return import.meta.env.DEV && window.location.hostname === 'localhost'
}
