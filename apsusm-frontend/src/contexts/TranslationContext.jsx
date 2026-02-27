import React, { createContext, useContext, useState, useEffect } from 'react'

const translations = {
  en: {
    // Navigation
    nav_home: 'Home',
    nav_how_it_works: 'How it works',
    nav_register: 'Register',
    nav_admin: 'Admin',
    nav_apply: 'Apply for Membership',
    nav_mock_mode: 'Mock Mode',

    // Homepage
    hero_tag: 'Membership open for',
    hero_tag_suffix: 'health professionals',
    hero_title: 'APSUSM Membership Portal',
    hero_subtitle: 'Join the Associação dos Profissionais de Saúde Unidos e Solidários de Moçambique. Register online, pay securely, and receive your verified digital membership card.',
    hero_cta_primary: 'Apply for Membership',
    hero_cta_secondary: 'How it works',

    stats_members: 'Registered Members',
    stats_provinces: 'Provinces Covered',
    stats_secure: 'Secure Payments',
    stats_card: 'Card Generation',

    features_title: 'What you get',
    features_subtitle: 'Everything included in your membership',
    feature_card_title: 'Digital Membership Card',
    feature_card_desc: 'Receive a professionally designed digital card with your photo, unique ID, and QR verification code.',
    feature_verify_title: 'Secure Verification',
    feature_verify_desc: 'Every card carries a scannable QR code that instantly verifies your membership status online.',
    feature_payment_title: 'Paystack Payments',
    feature_payment_desc: 'Safe and fast payment processing through Paystack — supports mobile money and card payments.',
    feature_email_title: 'Email Confirmation',
    feature_email_desc: 'Get your membership card and payment receipt delivered directly to your inbox within 24 hours.',

    process_title: 'Process',
    process_subtitle: 'How it works',
    process_1_title: 'Fill Registration Form',
    process_1_desc: 'Provide your medical accreditation details, contact info, and upload a clear portrait photo.',
    process_2_title: 'Complete Payment',
    process_2_desc: 'Pay the membership fee securely via Paystack. You\'ll be redirected back automatically.',
    process_3_title: 'Receive Your Card',
    process_3_desc: 'Your unique Member ID and digital membership card are emailed to you within minutes.',

    cta_title: 'Join today',
    cta_subtitle: 'Ready to become a verified APSUSM member?',
    cta_button: 'Apply Now',
    cta_note: 'Fast process · Secure payment · Digital card',

    // Registration Form
    form_title: 'APSUSM Membership Registration',
    form_subtitle: 'Complete the form below to apply for your APSUSM membership',
    step_personal: 'Personal Information',
    step_professional: 'Professional Details',
    step_contact: 'Contact Information',
    step_review: 'Review & Payment',
    
    // Form fields
    full_name: 'Full Name',
    email: 'Email Address',
    phone: 'Phone Number',
    date_of_birth: 'Date of Birth',
    gender: 'Gender',
    gender_male: 'Male',
    gender_female: 'Female',
    gender_other: 'Other',
    
    license_number: 'License Number',
    specialization: 'Specialization',
    institution: 'Institution/Hospital',
    province: 'Province',
    
    address: 'Address',
    city: 'City',
    postal_code: 'Postal Code',
    
    photo_upload: 'Upload Portrait Photo',
    photo_requirements: 'JPEG or PNG, max 5MB, clear face photo',
    
    form_next: 'Next',
    form_previous: 'Previous',
    form_submit: 'Submit Registration',
    form_processing: 'Processing...',
    
    // Payment
    payment_title: 'Complete Payment',
    payment_desc: 'Pay the membership fee to receive your digital membership card',
    payment_amount: 'Membership Fee',
    payment_button: 'Pay with Paystack',
    
    // Payment Verification
    verify_verifying: 'Verifying Payment...',
    verify_verifying_desc: 'Please wait while we confirm your payment with Paystack.',
    verify_success: 'Payment Verified!',
    verify_success_desc: 'Your membership card is being generated. Redirecting...',
    verify_failed: 'Payment Failed',
    verify_back: 'Back to Registration',
    
    // Success Page
    success_welcome: 'Welcome to APSUSM!',
    success_desc: 'Your membership has been confirmed. You are now a verified member.',
    success_member_id: 'Your Member ID',
    success_download_front: 'Download Front',
    success_download_back: 'Download Back',
    success_generating: 'Generating your membership card...',
    success_generating_desc: 'This page will update automatically.',
    success_email_sent: 'A confirmation email with your digital card has been sent to',
    
    // Verification Page
    verify_title: 'Membership Verification',
    verify_verified: 'Verified Member',
    verify_valid: 'This is a valid APSUSM credential',
    verify_not_found: 'Not Found',
    verify_not_found_desc: 'This member ID could not be verified',
    
    // Admin
    admin_title: 'Admin Portal',
    admin_subtitle: 'APSUSM Membership Management',
    admin_username: 'Username',
    admin_password: 'Password',
    admin_sign_in: 'Sign In',
    admin_invalid: 'Invalid credentials',
    
    admin_stats_total: 'Total Members',
    admin_stats_pending: 'Pending Payment',
    admin_stats_paid: 'Paid',
    admin_stats_active: 'Active',
    admin_stats_cards: 'Cards Generated',
    admin_stats_failed: 'Payment Failed',
    
    admin_members: 'Members',
    admin_search_placeholder: 'Search name, email, ID...',
    admin_filter_all: 'All Statuses',
    
    // Footer
    footer_copyright: 'Associação dos Profissionais de Saúde Unidos e Solidários de Moçambique.',
  },
  
  pt: {
    // Navigation
    nav_home: 'Início',
    nav_how_it_works: 'Como funciona',
    nav_register: 'Registrar',
    nav_admin: 'Admin',
    nav_apply: 'Inscrever-se',
    nav_mock_mode: 'Modo Teste',

    // Homepage
    hero_tag: 'Associação aberta para',
    hero_tag_suffix: 'profissionais de saúde',
    hero_title: 'Portal de Filiação APSUSM',
    hero_subtitle: 'Junte-se à Associação dos Profissionais de Saúde Unidos e Solidários de Moçambique. Registre-se online, pague com segurança e receba seu cartão de filiação digital verificado.',
    hero_cta_primary: 'Inscrever-se',
    hero_cta_secondary: 'Como funciona',

    stats_members: 'Membros Registrados',
    stats_provinces: 'Províncias Cobertas',
    stats_secure: 'Pagamentos Seguros',
    stats_card: 'Geração de Cartão',

    features_title: 'O que você recebe',
    features_subtitle: 'Tudo incluído na sua filiação',
    feature_card_title: 'Cartão de Filiação Digital',
    feature_card_desc: 'Receba um cartão digital profissionalmente projetado com sua foto, ID exclusivo e código QR de verificação.',
    feature_verify_title: 'Verificação Segura',
    feature_verify_desc: 'Cada cartão possui um código QR escaneável que verifica instantaneamente seu status de filiação online.',
    feature_payment_title: 'Pagamentos Paystack',
    feature_payment_desc: 'Processamento de pagamento rápido e seguro através do Paystack — suporta dinheiro móvel e pagamentos com cartão.',
    feature_email_title: 'Confirmação por Email',
    feature_email_desc: 'Receba seu cartão de filiação e comprovante de pagamento diretamente na sua caixa de entrada em até 24 horas.',

    process_title: 'Processo',
    process_subtitle: 'Como funciona',
    process_1_title: 'Preencher Formulário',
    process_1_desc: 'Forneça seus detalhes de credenciamento médico, informações de contato e carregue uma foto de retrato clara.',
    process_2_title: 'Completar Pagamento',
    process_2_desc: 'Pague a taxa de filiação com segurança através do Paystack. Você será redirecionado automaticamente.',
    process_3_title: 'Receber seu Cartão',
    process_3_desc: 'Seu ID de Membro exclusivo e cartão de filiação digital são enviados por email em minutos.',

    cta_title: 'Junte-se hoje',
    cta_subtitle: 'Pronto para se tornar um membro verificado da APSUSM?',
    cta_button: 'Inscrever-se Agora',
    cta_note: 'Processo rápido · Pagamento seguro · Cartão digital',

    // Registration Form
    form_title: 'Registro de Filiação APSUSM',
    form_subtitle: 'Preencha o formulário abaixo para solicitar sua filiação APSUSM',
    step_personal: 'Informações Pessoais',
    step_professional: 'Detalhes Profissionais',
    step_contact: 'Informações de Contato',
    step_review: 'Revisão e Pagamento',
    
    // Form fields
    full_name: 'Nome Completo',
    email: 'Endereço de Email',
    phone: 'Número de Telefone',
    date_of_birth: 'Data de Nascimento',
    gender: 'Gênero',
    gender_male: 'Masculino',
    gender_female: 'Feminino',
    gender_other: 'Outro',
    
    license_number: 'Número da Licença',
    specialization: 'Especialização',
    institution: 'Instituição/Hospital',
    province: 'Província',
    
    address: 'Endereço',
    city: 'Cidade',
    postal_code: 'Código Postal',
    
    photo_upload: 'Carregar Foto de Retrato',
    photo_requirements: 'JPEG ou PNG, máximo 5MB, foto clara do rosto',
    
    form_next: 'Próximo',
    form_previous: 'Anterior',
    form_submit: 'Enviar Registro',
    form_processing: 'Processando...',
    
    // Payment
    payment_title: 'Completar Pagamento',
    payment_desc: 'Pague a taxa de filiação para receber seu cartão de filiação digital',
    payment_amount: 'Taxa de Filiação',
    payment_button: 'Pagar com Paystack',
    
    // Payment Verification
    verify_verifying: 'Verificando Pagamento...',
    verify_verifying_desc: 'Aguarde enquanto confirmamos seu pagamento com o Paystack.',
    verify_success: 'Pagamento Verificado!',
    verify_success_desc: 'Seu cartão de filiação está sendo gerado. Redirecionando...',
    verify_failed: 'Pagamento Falhou',
    verify_back: 'Voltar ao Registro',
    
    // Success Page
    success_welcome: 'Bem-vindo à APSUSM!',
    success_desc: 'Sua filiação foi confirmada. Você agora é um membro verificado.',
    success_member_id: 'Seu ID de Membro',
    success_download_front: 'Baixar Frente',
    success_download_back: 'Baixar Verso',
    success_generating: 'Gerando seu cartão de filiação...',
    success_generating_desc: 'Esta página será atualizada automaticamente.',
    success_email_sent: 'Um email de confirmação com seu cartão digital foi enviado para',
    
    // Verification Page
    verify_title: 'Verificação de Filiação',
    verify_verified: 'Membro Verificado',
    verify_valid: 'Esta é uma credencial APSUSM válida',
    verify_not_found: 'Não Encontrado',
    verify_not_found_desc: 'Este ID de membro não pôde ser verificado',
    
    // Admin
    admin_title: 'Portal Admin',
    admin_subtitle: 'Gestão de Filiação APSUSM',
    admin_username: 'Nome de Usuário',
    admin_password: 'Senha',
    admin_sign_in: 'Entrar',
    admin_invalid: 'Credenciais inválidas',
    
    admin_stats_total: 'Total de Membros',
    admin_stats_pending: 'Pagamento Pendente',
    admin_stats_paid: 'Pago',
    admin_stats_active: 'Ativo',
    admin_stats_cards: 'Cartões Gerados',
    admin_stats_failed: 'Pagamento Falhou',
    
    admin_members: 'Membros',
    admin_search_placeholder: 'Buscar nome, email, ID...',
    admin_filter_all: 'Todos os Status',
    
    // Footer
    footer_copyright: 'Associação dos Profissionais de Saúde Unidos e Solidários de Moçambique.',
  }
}

const TranslationContext = createContext()

export function TranslationProvider({ children }) {
  const [language, setLanguage] = useState(() => {
    const saved = localStorage.getItem('apsusm_language')
    return saved || 'en' // Default to English
  })

  useEffect(() => {
    localStorage.setItem('apsusm_language', language)
    document.documentElement.lang = language
  }, [language])

  const t = (key) => {
    return translations[language][key] || key
  }

  const toggleLanguage = () => {
    setLanguage(prev => prev === 'en' ? 'pt' : 'en')
  }

  return (
    <TranslationContext.Provider value={{ language, t, toggleLanguage }}>
      {children}
    </TranslationContext.Provider>
  )
}

export function useTranslation() {
  const context = useContext(TranslationContext)
  if (!context) {
    throw new Error('useTranslation must be used within TranslationProvider')
  }
  return context
}
