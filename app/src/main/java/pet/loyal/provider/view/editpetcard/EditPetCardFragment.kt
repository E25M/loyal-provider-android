package pet.loyal.provider.view.editpetcard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.LightingColorFilter
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_edit_patiant_card.*
import kotlinx.android.synthetic.main.fragment_parent_self_invite.*
import pet.loyal.client.api.response.PetCardDataResponse
import pet.loyal.provider.BuildConfig
import pet.loyal.provider.R
import pet.loyal.provider.api.responses.AppVersionResponse
import pet.loyal.provider.databinding.FragmentEditPatiantCardBinding
import pet.loyal.provider.model.Phase
import pet.loyal.provider.model.PhaseMessage
import pet.loyal.provider.model.RequestPTBMessage
import pet.loyal.provider.util.*
import pet.loyal.provider.view.home.HomeScreen
import pet.loyal.provider.view.login.LoginActivity
import pet.loyal.provider.view.phasechange.PhaseListDialogFragment
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EditPetCardFragment : Fragment(), PhaseMessageRecyclerViewAdapter.PhaseMessageItemListener,
    PhaseListDialogFragment.PhaseListDialogFragmentListener, EditPetCardPermissionListener {

    override fun onPermissionGranted(granted: Boolean, requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_WRITE_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            }
            PERMISSION_REQUEST_READ_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            }
        }
    }

    override fun onActivityResultListener(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    private val CONTEXT_MENU_TAKE_A_PHOTO = 1
    private val CONTEXT_MENU_SELECT_A_PHOTO = 2
    private val REQUEST_TAKE_PICTURE = 1
    private val REQUEST_SELECT_PICTURE = 2
    private val PERMISSION_REQUEST_WRITE_STORAGE = 103
    private val PERMISSION_REQUEST_READ_STORAGE = 104
    private val REQUEST_CODE = 101

    private lateinit var fragmentEditPatiantCardBinding: FragmentEditPatiantCardBinding
    private lateinit var viewModel: EditPetCardViewModel
    private lateinit var preferenceManager: PreferenceManager

    private var selectedPhotoFile: File? = null
    private lateinit var selectedPhotoUri: Uri
    private var capturedImageCount = 0
    lateinit var selectedMessageId: String
    var selectedMessagePosition = 0
    private var imageGalleryList: HashMap<String, ArrayList<Uri>> = HashMap()
    private var imageIdsList: HashMap<String, ArrayList<String>> = HashMap()
    private var petCardDataResponse:PetCardDataResponse? = null
    private val phaseMessages = ArrayList<PhaseMessage>()
    private var uploadingImagePosition = 0
    private var uploadingMessageIdPosition = 0

    private var customMessageId = 0
    private lateinit var appointmentId: String

    companion object {
        fun newInstance() = EditPetCardFragment()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu.add(0, CONTEXT_MENU_TAKE_A_PHOTO, 0, getString(R.string.text_take_a_photo))
        menu.add(0, CONTEXT_MENU_SELECT_A_PHOTO, 0, getString(R.string.text_choose_a_photo))
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            CONTEXT_MENU_TAKE_A_PHOTO -> checkPermissions()
            CONTEXT_MENU_SELECT_A_PHOTO -> selectPhoto()
        }
        return true
    }

    private fun checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_WRITE_STORAGE)
            } else {
                captureImage()
            }
        } else {
            captureImage()
        }
    }

    //create file to save captured image by camera and save image
    private fun captureImage() {
        val root = Environment.getExternalStorageDirectory().toString()
        val loyalDir = File("$root${Constants.folder_loyal}")
        loyalDir.mkdirs()

        selectedPhotoFile = File(loyalDir, Constants.captured_pic_name + capturedImageCount + ".png")
//        selectedPhotoFile = File(loyalDir, Constants.captured_pic_name + ".png")
        if (selectedPhotoFile != null) {
            if (selectedPhotoFile!!.exists()) {
                selectedPhotoFile!!.delete()
            }
        }

        selectedPhotoFile!!.createNewFile()
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        selectedPhotoUri = FileProvider.getUriForFile(activity!!,
            BuildConfig.APPLICATION_ID + ".provider", selectedPhotoFile!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedPhotoUri)

        startActivityForResult(intent, REQUEST_TAKE_PICTURE)
    }

    private fun selectPhoto() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_READ_STORAGE)
            } else {
                openGallery()
            }
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_SELECT_PICTURE)
    }

    private fun initDataBinding(){
        viewModel = ViewModelProviders.of(this).get(EditPetCardViewModel::class.java)
        fragmentEditPatiantCardBinding.lifecycleOwner = this
        fragmentEditPatiantCardBinding.viewModel = viewModel
        preferenceManager = PreferenceManager(context!!)
        viewModel.liveColor.value = resources.getDrawable(R.drawable.bg_general, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        fragmentEditPatiantCardBinding = FragmentEditPatiantCardBinding
            .inflate(inflater, container, false)
        initDataBinding()

        if (arguments != null){
            appointmentId = arguments!!.getString(Constants.extra_appointment_id, "")
        }

        setObservers()
        loadAppointment()

        fragmentEditPatiantCardBinding.btnUpdate.setOnClickListener {
            val selectedPTBMessages = getSelectedMessages()
            if (selectedPTBMessages.isNotEmpty()) {
                if (imageGalleryList.size > 0){
                    uploadPhoto()
                }else{
                    savePTBMessages()
                }
            }else{
                showToast(activity!!, getString(R.string.error_no_selected_ptb_message))
            }
        }

        fragmentEditPatiantCardBinding.btnCancel.setOnClickListener {
            activity!!.onBackPressed()
        }

        fragmentEditPatiantCardBinding.layoutNext.setOnClickListener {
            val selectedPhaseList = ArrayList<Phase>()
            petCardDataResponse?.phases?.iterator()?.forEach { phase ->
                if (phase.id > petCardDataResponse?.appointment?.phase!!){
                    selectedPhaseList.add(phase)
                }
            }
            if (selectedPhaseList.size > 0) {
                loadPhaseChangeDialog(selectedPhaseList)
            }else{
                showToast(activity!!, "This is the final phase..")
            }
        }

        fragmentEditPatiantCardBinding.layoutPrevious.setOnClickListener {
            val selectedPhaseList = ArrayList<Phase>()
            petCardDataResponse?.phases?.iterator()?.forEach { phase ->
                if (phase.id < petCardDataResponse?.appointment?.phase!!){
                    selectedPhaseList.add(phase)
                }
            }
            if (selectedPhaseList.size > 0) {
                loadPhaseChangeDialog(selectedPhaseList)
            }else{
                showToast(activity!!, "This is the earliest phase..")
            }
        }

        (activity as HomeScreen).setEditCardPermissionListener(this)

        return fragmentEditPatiantCardBinding.root
    }

    private fun loadPhaseChangeDialog(phaseList: ArrayList<Phase>){
        val phaseListDialogFragment = PhaseListDialogFragment()
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constants.extra_phase_list, phaseList)
        bundle.putString(Constants.extra_appointment_id, petCardDataResponse?.appointment?.id)
        bundle.putString(Constants.extra_pet_name, petCardDataResponse?.appointment?.petName)
        phaseListDialogFragment.arguments = bundle
        phaseListDialogFragment.setTargetFragment(this, REQUEST_CODE)
        activity!!.supportFragmentManager.beginTransaction().add(phaseListDialogFragment, "PhaseListDialog").commit()
    }

    private fun loadAppointment(){
        refreshAll()
        if (appointmentId != null && appointmentId.isNotEmpty()) {
            viewModel.getPetCardById(appointmentId, preferenceManager.getLoginToken())
        }
    }

    private fun savePTBMessages(){
        viewModel.savePTMMessages(getSelectedMessages(), preferenceManager.getLoginToken(),
                petCardDataResponse?.appointment?.phase!!, petCardDataResponse?.appointment?.id!!,
            preferenceManager.getFacilityId())
    }

    private fun getSelectedMessages() :ArrayList<RequestPTBMessage>{

        val requestMessageList = ArrayList<RequestPTBMessage>()
        phaseMessages.iterator().forEach {
            phaseMessage ->
            run {
                if (phaseMessage.isSelected) {
                    val requestPTBMessage = RequestPTBMessage(
                        phaseMessage._id,
                        phaseMessage.message,
                        phaseMessage.getIsCustom(),
                        imageIdsList[phaseMessage._id]
                    )
                    requestMessageList.add(requestPTBMessage)
                }
            }
        }
        return requestMessageList
    }

    private fun setObservers(){
        viewModel.petCardResponse.observe(this, Observer {
                petCardResponse ->
            run {
                if (petCardResponse != null) {
                    if (petCardResponse.throwable != null) {
                        if (isJSONValid(petCardResponse.throwable?.message!!)) {
                            val signInResponse = Gson().fromJson(
                                petCardResponse.throwable?.message, AppVersionResponse::class.java
                            )
                            if (signInResponse.statusCode == 401) {
                                redirectToLogin()
                            }else if (signInResponse.statusCode == 400
                                && signInResponse.errorMessage == Constants.self_invite_parent_already_exist_inactive){
                                showPopup(activity!!, getString(R.string.msg_parent_inactive),
                                    getString(R.string.text_info))
                            }else{
                                showPopup(activity!!, petCardResponse.throwable?.message!!, getString(R.string.text_info))
                            }
                        } else {
                            showPopup(activity!!, petCardResponse.throwable?.message!!, getString(R.string.text_info))
                        }
                    } else if (petCardResponse.petCardResponse != null){
                        petCardDataResponse = petCardResponse.petCardResponse?.data
                        showCardDetails()
                    }
                }
                viewModel.liveProgressDialog.value = View.GONE
            }
        })

        viewModel.savePTBMessageResponse.observe(this, Observer { savePTBMessageResponse ->
            run {
                if (savePTBMessageResponse != null){
                    if (savePTBMessageResponse.throwable != null){
                        if (isJSONValid(savePTBMessageResponse.throwable?.message!!)) {
                            val signInResponse = Gson().fromJson(
                                savePTBMessageResponse.throwable?.message, AppVersionResponse::class.java
                            )
                            if (signInResponse.statusCode == 401) {
                                redirectToLogin()
                            }else if (signInResponse.statusCode == 400
                                && signInResponse.errorMessage == Constants.self_invite_parent_already_exist_inactive){
                                showPopup(activity!!, getString(R.string.msg_parent_inactive),
                                    getString(R.string.text_info))
                            }else{
                                showPopup(activity!!, savePTBMessageResponse.throwable?.message!!, getString(R.string.text_info))
                            }
                        } else {
                            showPopup(activity!!, savePTBMessageResponse.throwable?.message!!, getString(R.string.text_info))
                        }
                    }else{
                        loadAppointment()
                    }
                }
                viewModel.liveProgressDialog.value = View.GONE
            }
        })
    }

    private fun showCardDetails(){
        if (petCardDataResponse != null){
            val petCardDataResponse = this.petCardDataResponse!!
            val appointment = petCardDataResponse.appointment
            if (appointment != null){
                val phases = petCardDataResponse.phases
                viewModel.livePetName.value = appointment.petName + ", " + appointment.parentLastName
                viewModel.livePetImage.value = appointment.petImage

                if (appointment.petBreed != null) {
                    viewModel.liveBreedSpecies.value = appointment.petBreed +
                            ", " + appointment.petSpecies + "\n" + appointment.petGender
                }else{
                    viewModel.liveBreedSpecies.value = appointment.petSpecies + "\n" + appointment.petGender
                }
//                viewModel.liveGendar.value = appointment.petGender
                phases.iterator().forEach {
                    if (it.id == appointment.phase){
                        viewModel.livePhase.value = it.name
                    }
                }

                val bgColor:Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getPhaseColors(appointment.phase, activity!!)
                }else{
                    getPhaseColorsOld(appointment.phase, activity!!)
                }

                val drawableBgType =  resources.getDrawable(R.drawable.bg_general, null)
                val colorFilter = LightingColorFilter(bgColor, bgColor)
                drawableBgType.colorFilter = colorFilter
                viewModel.liveColor.value = drawableBgType

                val drawableTop =  resources.getDrawable(R.drawable.bg_edit_patient_card_deco, null)
                drawableTop.colorFilter = colorFilter
                viewModel.livePhaseColorDrawable.value = drawableTop

                petCardDataResponse.ptbSentMessages.iterator().forEach { sentMessage ->
                    val imageGallery = ArrayList<Uri>()
                    if (sentMessage.gallery != null) {
                        sentMessage.gallery.iterator().forEach {
                            imageGallery.add(Uri.parse(Constants.url_cloudinary_news_feed + it))
                        }
                    }

                    var message = sentMessage.message
                    message = message.replace("PetName",
                        petCardDataResponse.appointment.petName, true)

                    phaseMessages.add(PhaseMessage(sentMessage.phaseMessageId, sentMessage.status,
                        sentMessage._id, sentMessage.phaseId, sentMessage.appointmentId,
                        message, sentMessage.dateTime, imageGallery))
                }

                petCardDataResponse.ptbMessageTemplates.iterator().forEach { messageTemplate ->
                    run {
                        var imageGallery: ArrayList<Uri>? = null
                        if (imageGalleryList.containsKey(messageTemplate._id)) {
                            imageGallery = imageGalleryList[messageTemplate._id]
                        }

                        var message = messageTemplate.message
                        message = message.replace("PetName",
                            petCardDataResponse.appointment.petName, true)

                        if (messageTemplate.fetchable){
                            var replaceableText = ""
                            when(messageTemplate.fetchableValue){
                                "PhoneNumber" -> replaceableText =
                                    preferenceManager.getFacilityPhone()
                                "FacilityName" -> replaceableText =
                                    preferenceManager.getFacilityName()
                            }
                            message = message.replace(
                                messageTemplate.fetchableValue,
                                replaceableText,
                                true)
                        }

                        phaseMessages.add(
                            PhaseMessage(
                                messageTemplate._id, messageTemplate.phaseId,
                                message, messageTemplate.editable, imageGallery
                            )
                        )
                    }
                }

                addCustomMessage()

                val phaseMessageRecyclerViewAdapter = PhaseMessageRecyclerViewAdapter(
                    phaseMessages, this)
                fragmentEditPatiantCardBinding.recyclerViewMessages.setHasFixedSize(true)
                fragmentEditPatiantCardBinding.recyclerViewMessages.layoutManager =
                    LinearLayoutManager(context)
                fragmentEditPatiantCardBinding.recyclerViewMessages.adapter =
                    phaseMessageRecyclerViewAdapter
            }
        }
    }

    private fun refreshAll(){
        imageGalleryList.clear()
        imageIdsList.clear()
        phaseMessages.clear()

        uploadingImagePosition = 0
        uploadingMessageIdPosition = 0
        petCardDataResponse = null
    }

    private fun addCustomMessage(){
        customMessageId++
        phaseMessages.add(
            PhaseMessage(customMessageId.toString(), petCardDataResponse?.appointment?.phase!!,
                petCardDataResponse?.appointment?.id)
        )
    }

    private fun showAddedImage(){
        phaseMessages.iterator().forEach { phaseMessage ->
            if (phaseMessage._id == selectedMessageId){
                phaseMessage.imageGallery = imageGalleryList[selectedMessageId]
            }
        }
        notifyItemChange()
    }

    private fun notifyItemChange(){
        if (fragmentEditPatiantCardBinding.recyclerViewMessages.adapter != null) {
            fragmentEditPatiantCardBinding.recyclerViewMessages.adapter!!
                .notifyItemChanged(selectedMessagePosition)
        }
    }

    private fun uploadPhoto() {

        viewModel.liveProgressDialog.value = View.VISIBLE

        val publicId = "${Calendar.getInstance().timeInMillis}_${preferenceManager.getUserId()}"
        val imageUri = imageGalleryList[imageGalleryList.keys
            .elementAt(uploadingMessageIdPosition)]?.get(uploadingImagePosition)!!

        MediaManager.get().upload(imageUri)
            .option("public_id", publicId)
            .option("folder", "newsfeed/")
            .unsigned(Constants.upload_preset)
            .callback(object : UploadCallback {

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val messageId =  imageGalleryList.keys.elementAt(uploadingMessageIdPosition)

                    if (!imageIdsList.containsKey(messageId)){
                        imageIdsList[messageId] = arrayListOf()
                    }
                    imageIdsList[messageId]?.add(publicId)

                    if (uploadingMessageIdPosition + 1 == imageGalleryList.keys.size
                        && uploadingImagePosition + 1 == imageGalleryList[messageId]?.size){

                        savePTBMessages()
                    }else{
                        if (imageGalleryList[messageId]?.size == uploadingImagePosition + 1){
                            uploadingMessageIdPosition ++
                            uploadingImagePosition = 0
                        }
                        uploadingImagePosition ++

                        uploadPhoto()
                    }
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    viewModel.liveProgressDialog.value = View.GONE
                    showPopup(activity!!, getString(R.string.error_upload_failed), getString(R.string.text_error))
                }

                override fun onStart(requestId: String?) {}

            }).dispatch()
    }

    private fun redirectToLogin() {
        preferenceManager.deleteSession()
        showToast(context!!, getString(R.string.txt_logged_out))
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }

    override fun onClickAddPhotos(view: View, position: Int, messageId: String) {
        selectedMessageId = messageId
        selectedMessagePosition = position
        registerForContextMenu(view)
        activity!!.openContextMenu(view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_PICTURE && resultCode ==
            Activity.RESULT_OK && data != null) {
            if (data.data != null){
                val imageUri = data.data.toString()
                addImageUriToGallery(Uri.parse(imageUri))
            }else if(data.clipData != null){
                val itemCount = data.clipData.itemCount
                for (item in itemCount downTo 1 step 1){
                    addImageUriToGallery(data.clipData.getItemAt(item - 1).uri)
                }
            }
        }

        if (requestCode == REQUEST_TAKE_PICTURE && resultCode == Activity.RESULT_OK){
            addImageUriToGallery(selectedPhotoUri)
            capturedImageCount ++
//            resetOrientation(selectedPhotoFile)
        }

        showAddedImage()
    }

    private fun addImageUriToGallery(uri: Uri?){
        if (uri != null){
            if (!imageGalleryList.containsKey(selectedMessageId)){
                imageGalleryList[selectedMessageId] = ArrayList<Uri>()
            }
            if (imageGalleryList[selectedMessageId]?.size!! < 10) {
                imageGalleryList[selectedMessageId]?.add(0, uri)
            }else{
                showToast(activity!!, getString(R.string.text_10_photos_limit))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_WRITE_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            }
            PERMISSION_REQUEST_READ_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            }
        }
    }

    override fun onClickDelete(positionImage: Int, position: Int, messageId: String) {
        imageGalleryList[messageId]?.removeAt(positionImage)
        notifyItemChange()
    }

    override fun onClickImage(positionImage: Int, position: Int, messageId: String) {

    }

    override fun onClickTick(isChecked: Boolean, position: Int, messageId: String) {
        phaseMessages.iterator().forEach { phaseMessage ->
            run {
                if (phaseMessage._id == messageId) {
                    phaseMessage.isSelected = isChecked
                }
            }
        }
    }

    override fun onEditMessage(message: String, position: Int, messageId: String) {
        phaseMessages.iterator().forEach { phaseMessage ->
            if (phaseMessage._id == messageId){
                phaseMessage.message = message
            }
        }
    }

    override fun onPhaseChangeSuccess() {
        showPopup(activity!!, "Update sent to ${petCardDataResponse?.appointment?.petName}"
                + "'s support network ${getCurrentDateString()} ${getCurrentTimeString()}",
            getString(R.string.text_info))
    }

    override fun onPhaseChangeFailed(errorMessage: String) {
        showPopup(activity!!, getPhaseChangeError(errorMessage), getString(R.string.text_error))
    }

    private fun getPhaseChangeError(errorMessage: String): String {
        return when(errorMessage){
            Constants.pet_is_not_active -> "${petCardDataResponse?.appointment?.petName} " +
                    getString(R.string.text_please_active_pet)
            Constants.parent_is_not_active -> "${petCardDataResponse?.appointment?.parentFirstName}" +
                    " ${petCardDataResponse?.appointment?.parentLastName} " +
                    getString(R.string.text_please_active_parent)
            Constants.there_are_another_ongoing_appointments_for_this_pet ->
                "${petCardDataResponse?.appointment?.petName} " +
                        getString(R.string.text_please_complete_ongoing_appointment)
            else -> getString(R.string.text_phase_change_failed)
        }
    }

    override fun onAddCustomMessage(position: Int) {
        addCustomMessage()
    }

    fun showPopup(context: Context, message: String, title: String) {
        val aDialog = AlertDialog.Builder(context)
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                loadAppointment()
            }
            .create()
        aDialog.show()
    }
}
