import React, { useEffect, useState } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { Loader2, CheckCircle, XCircle } from 'lucide-react'
import { verifyPayment } from '../api'

export default function PaymentVerifyPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const [status, setStatus] = useState('verifying') // verifying, success, failed
  const [message, setMessage] = useState('')
  const [memberId, setMemberId] = useState(null)

  useEffect(() => {
    const reference = searchParams.get('reference') || searchParams.get('trxref')
    if (!reference) {
      setStatus('failed')
      setMessage('No payment reference found')
      return
    }

    const verify = async () => {
      try {
        const result = await verifyPayment(reference)
        if (result.success) {
          setStatus('success')
          setMemberId(result.member?.id)
          setTimeout(() => {
            navigate(`/success/${result.member?.id}`)
          }, 2000)
        } else {
          setStatus('failed')
          setMessage(result.message || 'Payment verification failed')
        }
      } catch (err) {
        setStatus('failed')
        setMessage(err.response?.data?.message || 'Verification failed. Please contact support.')
      }
    }

    verify()
  }, [searchParams, navigate])

  return (
    <div className="min-h-screen pt-32 pb-20 flex items-center justify-center">
      <div className="max-w-md mx-auto px-6 text-center animate-fade-in">
        {status === 'verifying' && (
          <>
            <Loader2 className="w-16 h-16 text-brand-blue mx-auto animate-spin mb-6" />
            <h2 className="text-2xl font-medium text-slate-900 mb-3">Verifying Payment...</h2>
            <p className="text-slate-500">Please wait while we confirm your payment with Paystack.</p>
          </>
        )}

        {status === 'success' && (
          <>
            <div className="w-20 h-20 bg-green-50 rounded-full flex items-center justify-center mx-auto mb-6">
              <CheckCircle className="w-12 h-12 text-green-500" />
            </div>
            <h2 className="text-2xl font-medium text-slate-900 mb-3">Payment Verified!</h2>
            <p className="text-slate-500">Your membership card is being generated. Redirecting...</p>
          </>
        )}

        {status === 'failed' && (
          <>
            <div className="w-20 h-20 bg-red-50 rounded-full flex items-center justify-center mx-auto mb-6">
              <XCircle className="w-12 h-12 text-red-500" />
            </div>
            <h2 className="text-2xl font-medium text-slate-900 mb-3">Payment Failed</h2>
            <p className="text-slate-500 mb-6">{message}</p>
            <button
              onClick={() => navigate('/')}
              className="px-6 py-3 bg-slate-900 text-white rounded-lg text-sm font-medium hover:bg-slate-800 transition-all"
            >
              Back to Registration
            </button>
          </>
        )}
      </div>
    </div>
  )
}
