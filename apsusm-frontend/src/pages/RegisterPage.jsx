import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Upload, Loader2, CheckCircle, AlertCircle, Camera, Shield, CreditCard, ArrowRight } from 'lucide-react'
import { registerMember, initializePayment } from '../api'

const PROVINCES = [
  'Maputo Cidade', 'Maputo Província', 'Gaza', 'Inhambane',
  'Sofala', 'Manica', 'Tete', 'Zambezia',
  'Nampula', 'Cabo Delgado', 'Niassa'
]

const SPECIALIZATIONS = [
  'General Medicine', 'Cardiology', 'Neurology', 'Pediatrics',
  'Surgery', 'Obstetrics & Gynecology', 'Orthopedics', 'Dermatology',
  'Ophthalmology', 'Psychiatry', 'Radiology', 'Anesthesiology',
  'Pathology', 'Emergency Medicine', 'Internal Medicine', 'Nursing',
  'Pharmacy', 'Dentistry', 'Public Health', 'Other'
]

export default function RegisterPage() {
  const navigate = useNavigate()
  const [step, setStep] = useState(1) // 1=form, 2=review, 3=payment
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [photoPreview, setPhotoPreview] = useState(null)
  const [registeredId, setRegisteredId] = useState(null)

  const [form, setForm] = useState({
    firstName: '', lastName: '', email: '', phone: '',
    licenseNumber: '', institution: '', specialization: '',
    province: '', photo: null
  })

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
    setError('')
  }

  const handlePhoto = (e) => {
    const file = e.target.files[0]
    if (!file) return

    if (!['image/jpeg', 'image/png', 'image/jpg'].includes(file.type)) {
      setError('Only JPEG and PNG images are allowed')
      return
    }
    if (file.size > 5 * 1024 * 1024) {
      setError('File size must be under 5MB')
      return
    }

    setForm({ ...form, photo: file })
    setPhotoPreview(URL.createObjectURL(file))
    setError('')
  }

  const validateForm = () => {
    if (!form.firstName.trim()) return 'First name is required'
    if (!form.lastName.trim()) return 'Last name is required'
    if (!form.email.trim()) return 'Email is required'
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) return 'Invalid email format'
    if (!form.licenseNumber.trim()) return 'License number is required'
    if (!form.province) return 'Province is required'
    if (!form.photo) return 'Portrait photo is required'
    return null
  }

  const handleReview = () => {
    const err = validateForm()
    if (err) { setError(err); return }
    setStep(2)
  }

  const handleSubmit = async () => {
    setLoading(true)
    setError('')

    try {
      const formData = new FormData()
      formData.append('firstName', form.firstName)
      formData.append('lastName', form.lastName)
      formData.append('email', form.email)
      formData.append('phone', form.phone)
      formData.append('licenseNumber', form.licenseNumber)
      formData.append('institution', form.institution)
      formData.append('specialization', form.specialization)
      formData.append('province', form.province)
      formData.append('photo', form.photo)

      const result = await registerMember(formData)

      if (result.success) {
        setRegisteredId(result.memberId)
        setStep(3)

        // Initialize payment
        const payResult = await initializePayment(result.memberId)
        if (payResult.success && payResult.authorizationUrl) {
          // Redirect to Paystack
          window.location.href = payResult.authorizationUrl
        } else {
          setError(payResult.message || 'Payment initialization failed')
        }
      } else {
        setError(result.message || 'Registration failed')
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen pt-24 pb-20 bg-white">
      {/* Hero */}
      <div className="relative overflow-hidden">
        <div className="absolute top-0 left-0 w-full h-full -z-10 pointer-events-none">
          <div className="absolute top-[-10%] right-[-5%] w-[600px] h-[600px] bg-gradient-radial from-brand-blue/5 to-transparent opacity-60 blur-3xl" />
          <div className="absolute bottom-[-10%] left-[-10%] w-[600px] h-[600px] bg-gradient-radial from-brand-green/5 to-transparent opacity-60 blur-3xl" />
        </div>

        <div className="max-w-4xl mx-auto px-6 text-center py-12 animate-fade-in">
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-slate-50 border border-slate-200/80 text-xs font-medium text-slate-500 mb-6 tracking-wide">
            <span className="w-1.5 h-1.5 rounded-full bg-brand-blue animate-pulse" />
            Membership Registration
          </div>
          <h1 className="text-4xl md:text-5xl font-medium text-slate-900 tracking-tight leading-tight mb-4">
            Join the Standard.
          </h1>
          <p className="text-lg text-slate-500 font-light max-w-xl mx-auto">
            Complete your registration to become a verified APSUSM member and receive your digital credential.
          </p>
        </div>
      </div>

      {/* Progress Steps */}
      <div className="max-w-xl mx-auto px-6 mb-10">
        <div className="flex items-center justify-center gap-0">
          {[
            { num: 1, label: 'Details' },
            { num: 2, label: 'Review' },
            { num: 3, label: 'Payment' }
          ].map((s, i) => (
            <React.Fragment key={s.num}>
              <div className="flex flex-col items-center gap-2">
                <div className={`w-10 h-10 rounded-full flex items-center justify-center text-sm font-semibold transition-all ${
                  step >= s.num
                    ? 'bg-slate-900 text-white'
                    : 'bg-slate-100 text-slate-400'
                }`}>
                  {step > s.num ? <CheckCircle className="w-5 h-5" /> : s.num}
                </div>
                <span className={`text-xs font-medium ${step >= s.num ? 'text-slate-900' : 'text-slate-400'}`}>
                  {s.label}
                </span>
              </div>
              {i < 2 && (
                <div className={`w-20 h-0.5 mb-6 mx-2 ${step > s.num ? 'bg-slate-900' : 'bg-slate-200'}`} />
              )}
            </React.Fragment>
          ))}
        </div>
      </div>

      {/* Form */}
      <div className="max-w-xl mx-auto px-6 animate-slide-up">
        {error && (
          <div className="mb-6 flex items-center gap-3 p-4 bg-red-50 border border-red-200 rounded-xl text-sm text-red-700">
            <AlertCircle className="w-5 h-5 shrink-0" />
            {error}
          </div>
        )}

        {step === 1 && (
          <div className="bg-white p-8 rounded-2xl shadow-sm border border-slate-200 space-y-6">
            <div className="grid grid-cols-2 gap-6">
              <div className="space-y-2">
                <label className="text-xs font-medium text-slate-700">First Name *</label>
                <input
                  name="firstName" value={form.firstName} onChange={handleChange}
                  className="w-full px-4 py-3 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-4 focus:ring-slate-900/5 focus:border-slate-400 transition-all placeholder:text-slate-400"
                  placeholder="Jane"
                />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-medium text-slate-700">Last Name *</label>
                <input
                  name="lastName" value={form.lastName} onChange={handleChange}
                  className="w-full px-4 py-3 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-4 focus:ring-slate-900/5 focus:border-slate-400 transition-all placeholder:text-slate-400"
                  placeholder="Doe"
                />
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-xs font-medium text-slate-700">Professional Email *</label>
              <input
                name="email" type="email" value={form.email} onChange={handleChange}
                className="w-full px-4 py-3 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-4 focus:ring-slate-900/5 focus:border-slate-400 transition-all placeholder:text-slate-400"
                placeholder="doctor@clinic.com"
              />
            </div>

            <div className="space-y-2">
              <label className="text-xs font-medium text-slate-700">Phone Number</label>
              <input
                name="phone" value={form.phone} onChange={handleChange}
                className="w-full px-4 py-3 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-4 focus:ring-slate-900/5 focus:border-slate-400 transition-all placeholder:text-slate-400"
                placeholder="+258 84 000 0000"
              />
            </div>

            <div className="space-y-2">
              <label className="text-xs font-medium text-slate-700">Medical License Number *</label>
              <input
                name="licenseNumber" value={form.licenseNumber} onChange={handleChange}
                className="w-full px-4 py-3 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-4 focus:ring-slate-900/5 focus:border-slate-400 transition-all placeholder:text-slate-400 font-mono tracking-wide"
                placeholder="MZ-0000-00"
              />
            </div>

            <div className="space-y-2">
              <label className="text-xs font-medium text-slate-700">Institution</label>
              <input
                name="institution" value={form.institution} onChange={handleChange}
                className="w-full px-4 py-3 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-4 focus:ring-slate-900/5 focus:border-slate-400 transition-all placeholder:text-slate-400"
                placeholder="Hospital Central de Maputo"
              />
            </div>

            <div className="space-y-2">
              <label className="text-xs font-medium text-slate-700">Specialization</label>
              <select
                name="specialization" value={form.specialization} onChange={handleChange}
                className="w-full px-4 py-3 bg-white border border-slate-200 rounded-lg text-sm text-slate-600 appearance-none focus:outline-none focus:ring-4 focus:ring-slate-900/5 focus:border-slate-400 transition-all cursor-pointer"
              >
                <option value="">Select specialization...</option>
                {SPECIALIZATIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>

            <div className="space-y-2">
              <label className="text-xs font-medium text-slate-700">Province of Practice *</label>
              <select
                name="province" value={form.province} onChange={handleChange}
                className="w-full px-4 py-3 bg-white border border-slate-200 rounded-lg text-sm text-slate-600 appearance-none focus:outline-none focus:ring-4 focus:ring-slate-900/5 focus:border-slate-400 transition-all cursor-pointer"
              >
                <option value="">Select province...</option>
                {PROVINCES.map(p => <option key={p} value={p}>{p}</option>)}
              </select>
            </div>

            {/* Photo Upload */}
            <div className="space-y-2">
              <label className="text-xs font-medium text-slate-700">Portrait Photo * (JPEG/PNG, max 5MB)</label>
              <div className="flex items-center gap-4">
                <label className="flex-1 cursor-pointer">
                  <div className={`border-2 border-dashed rounded-xl p-6 text-center transition-all ${
                    photoPreview ? 'border-brand-blue/30 bg-blue-50/30' : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50'
                  }`}>
                    {photoPreview ? (
                      <div className="flex items-center gap-4">
                        <img src={photoPreview} alt="Preview" className="w-20 h-24 object-cover rounded-lg border border-slate-200" />
                        <div className="text-left">
                          <p className="text-sm font-medium text-slate-700">{form.photo?.name}</p>
                          <p className="text-xs text-slate-500 mt-1">{(form.photo?.size / 1024 / 1024).toFixed(2)} MB</p>
                          <p className="text-xs text-brand-blue mt-1">Click to change</p>
                        </div>
                      </div>
                    ) : (
                      <>
                        <Camera className="w-8 h-8 text-slate-400 mx-auto mb-2" />
                        <p className="text-sm text-slate-500">Click to upload your portrait photo</p>
                        <p className="text-xs text-slate-400 mt-1">High-quality ID-style photo from shoulders up</p>
                      </>
                    )}
                  </div>
                  <input type="file" accept="image/jpeg,image/png,image/jpg" onChange={handlePhoto} className="hidden" />
                </label>
              </div>
            </div>

            <button
              onClick={handleReview}
              className="w-full bg-slate-900 text-white font-medium py-3.5 rounded-lg hover:bg-slate-800 transition-all shadow-lg shadow-slate-200 active:scale-[0.99] flex items-center justify-center gap-2"
            >
              Review Details
              <ArrowRight className="w-4 h-4" />
            </button>
          </div>
        )}

        {step === 2 && (
          <div className="bg-white p-8 rounded-2xl shadow-sm border border-slate-200 space-y-6">
            <h3 className="text-lg font-semibold text-slate-900">Review Your Details</h3>

            <div className="flex items-center gap-4 p-4 bg-slate-50 rounded-xl">
              {photoPreview && (
                <img src={photoPreview} alt="Portrait" className="w-20 h-24 object-cover rounded-lg border border-slate-200" />
              )}
              <div>
                <p className="text-lg font-semibold text-slate-900">{form.firstName} {form.lastName}</p>
                <p className="text-sm text-slate-500">{form.specialization || 'Medical Professional'}</p>
              </div>
            </div>

            <div className="space-y-3">
              {[
                ['Email', form.email],
                ['Phone', form.phone || 'N/A'],
                ['License Number', form.licenseNumber],
                ['Institution', form.institution || 'N/A'],
                ['Province', form.province],
              ].map(([label, value]) => (
                <div key={label} className="flex justify-between py-2 border-b border-slate-100">
                  <span className="text-sm text-slate-500">{label}</span>
                  <span className="text-sm font-medium text-slate-700">{value}</span>
                </div>
              ))}
            </div>

            <div className="p-4 bg-blue-50 border border-blue-200 rounded-xl">
              <div className="flex items-center gap-2 text-sm font-medium text-blue-800 mb-1">
                <CreditCard className="w-4 h-4" />
                Payment Required
              </div>
              <p className="text-xs text-blue-600">
                After confirming, you will be redirected to Paystack to complete your membership payment.
                Your card will be generated after successful payment.
              </p>
            </div>

            <div className="flex gap-3">
              <button
                onClick={() => setStep(1)}
                className="flex-1 py-3.5 rounded-lg border border-slate-200 text-sm font-medium text-slate-600 hover:bg-slate-50 transition-all"
              >
                Edit Details
              </button>
              <button
                onClick={handleSubmit}
                disabled={loading}
                className="flex-1 bg-slate-900 text-white font-medium py-3.5 rounded-lg hover:bg-slate-800 transition-all shadow-lg shadow-slate-200 disabled:opacity-50 flex items-center justify-center gap-2"
              >
                {loading ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin" />
                    Processing...
                  </>
                ) : (
                  <>
                    Confirm & Pay
                    <Shield className="w-4 h-4" />
                  </>
                )}
              </button>
            </div>
          </div>
        )}

        {step === 3 && (
          <div className="bg-white p-8 rounded-2xl shadow-sm border border-slate-200 text-center space-y-4">
            <Loader2 className="w-12 h-12 text-brand-blue mx-auto animate-spin" />
            <h3 className="text-lg font-semibold text-slate-900">Redirecting to Payment...</h3>
            <p className="text-sm text-slate-500">You will be redirected to Paystack to complete your membership payment.</p>
            {error && (
              <div className="mt-4 p-4 bg-red-50 border border-red-200 rounded-xl text-sm text-red-700">
                {error}
                <button
                  onClick={() => { setStep(2); setError('') }}
                  className="block mt-2 text-brand-blue underline text-xs"
                >
                  Go back and try again
                </button>
              </div>
            )}
          </div>
        )}

        <div className="text-center mt-6">
          <p className="text-xs text-slate-400 flex items-center justify-center gap-1.5">
            <Shield className="w-3.5 h-3.5" />
            Your data is end-to-end encrypted and strictly confidential.
          </p>
        </div>
      </div>
    </div>
  )
}
